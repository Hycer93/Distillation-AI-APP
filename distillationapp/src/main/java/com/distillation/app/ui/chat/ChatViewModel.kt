package com.distillation.app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distillation.app.data.api.ILLMProvider
import com.distillation.app.data.api.Message
import com.distillation.app.data.persona.ContextManager
import com.distillation.app.data.persona.MemoryReviver
import com.distillation.app.data.persona.PersonaManager
import com.distillation.app.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 聊天界面状态
 * M1: 管理消息列表和加载状态
 * M4: 新增contextStats用于显示Token用量
 */
data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val contextStats: ContextManager.ContextStats = ContextManager.ContextStats(0, 0, 0, 0, 0)
)

/**
 * 聊天消息展示模型
 */
data class ChatMessage(
    val role: String,
    val content: String
)

/**
 * 聊天ViewModel
 * M1: 管理对话状态，调用LLM Provider
 * M3: 集成PersonaManager加载System Prompt，集成ChatRepository持久化对话
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val llmProvider: ILLMProvider,
    private val personaManager: PersonaManager,     // M3新增
    private val chatRepository: ChatRepository,      // M2新增
    private val memoryReviver: MemoryReviver,         // M4新增
    private val contextManager: ContextManager        // M4新增
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // 对话历史（用于多轮对话）
    private val conversationHistory = mutableListOf<Message>()

    /** 标记System Prompt是否已加载 */
    private var systemPromptLoaded = false

    /** 当前会话ID */
    private var currentConversationId: String? = null

    /** M4：显式回溯触发词 */
    private val reviveTriggerWords = listOf("复活", "醒来", "回溯", "之前说过", "还记得", "翻记录", "查对话")

    /** M4：更新当前上下文统计 */
    private fun updateContextStats() {
        _uiState.value = _uiState.value.copy(
            contextStats = contextManager.getContextStats()
        )
    }

    /**
     * 初始化会话 — 恢复最近会话或创建新会话，并注入System Prompt
     * M3: 在MainActivity.onCreate中调用
     *
     * 启动流程：
     * 1. 查找最近一次会话（Room中按createdAt DESC取第一条）
     * 2. 有最近会话 → 复用并加载历史20条消息到UI和conversationHistory
     * 3. 无会话 → 创建新会话
     */
    fun initConversation() {
        viewModelScope.launch {
            // 1. 尝试恢复最近会话
            val latestConversation = chatRepository.getLatestConversation()
            if (latestConversation != null) {
                currentConversationId = latestConversation.id
            } else {
                // 无历史会话，创建新会话
                val newConversation = chatRepository.createConversation("新对话")
                currentConversationId = newConversation.id
            }

            // 2. 注入System Prompt（仅首次）
            if (!systemPromptLoaded) {
                val systemPrompt = personaManager.assembleSystemPrompt()
                val revivePrompt = memoryReviver.generateRevivePrompt()
                conversationHistory.add(Message("system", systemPrompt))
                contextManager.addMessage(Message("system", systemPrompt))
                if (revivePrompt.isNotBlank()) {
                    conversationHistory.add(Message("system", revivePrompt))
                    contextManager.addMessage(Message("system", revivePrompt))
                }
                systemPromptLoaded = true
            }

            // 3. 加载历史20条消息到UI和对话上下文
            loadHistoryMessages()
            updateContextStats()
        }
    }

    /**
     * 发送消息
     * M3: 首次发送时自动初始化（若initConversation尚未完成）
     * M4: 检查显式回溯触发词，集成ContextManager
     */
    fun sendMessage(userInput: String) {
        if (userInput.isBlank()) return

        // 首次对话自动初始化
        if (!systemPromptLoaded) {
            viewModelScope.launch {
                initConversation()
                doSendMessage(userInput)
            }
            return
        }

        viewModelScope.launch {
            // M4: 检查显式回溯触发词
            if (checkReviveTrigger(userInput)) {
                handleMemoryRevive()
            }
            doSendMessage(userInput)
        }
    }

    private suspend fun doSendMessage(userInput: String) {
        // 添加用户消息到界面
        val userMessage = ChatMessage("user", userInput)
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            isLoading = true
        )

        // 添加到对话历史和ContextManager
        val userMsg = Message("user", userInput)
        conversationHistory.add(userMsg)
        contextManager.addMessage(userMsg)

        // 异步保存用户消息到数据库
        if (currentConversationId != null) {
            chatRepository.saveMessage(currentConversationId!!, "user", userInput)
        }

        try {
            // 调用API（携带完整对话历史，含System Prompt）
            val reply = llmProvider.chat(contextManager.getCurrentContext())

            // 添加AI回复到界面
            val aiMessage = ChatMessage("assistant", reply)
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + aiMessage,
                isLoading = false,
                contextStats = contextManager.getContextStats()
            )

            // 添加到对话历史和ContextManager
            val assistantMsg = Message("assistant", reply)
            conversationHistory.add(assistantMsg)
            contextManager.addMessage(assistantMsg)

            // 异步保存AI回复到数据库
            if (currentConversationId != null) {
                chatRepository.saveMessage(currentConversationId!!, "assistant", reply)
            }
        } catch (e: Exception) {
            val errorMessage = ChatMessage(
                "assistant",
                "厂长，连接出了点问题：${e.message}"
            )
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + errorMessage,
                isLoading = false
            )
        }
    }

    /**
     * M4：显式触发记忆回溯
     * 当用户输入包含触发词时，自动检索历史记忆注入上下文
     */
    private fun checkReviveTrigger(userInput: String): Boolean {
        return reviveTriggerWords.any { userInput.contains(it) }
    }

    private suspend fun handleMemoryRevive() {
        val revivePrompt = memoryReviver.generateRevivePrompt()
        val systemMessage = Message("system", revivePrompt)
        conversationHistory.add(systemMessage)
        contextManager.addMessage(systemMessage)
    }

    /**
     * 从Room数据库加载历史对话记录
     * 在initConversation()中调用，确保APP重启后对话历史延续
     *
     * 同步三处：
     * - conversationHistory：LLM API的对话上下文
     * - contextManager：Token用量统计与上下文裁剪
     * - _uiState.messages：界面消息列表
     */
    private suspend fun loadHistoryMessages() {
        val conversationId = currentConversationId ?: return

        // 读取最近20条消息（约10轮对话）
        val recentMessages = chatRepository.getRecentMessages(conversationId, limit = 20)

        if (recentMessages.isNotEmpty()) {
            // 注入LLM API上下文 + Token管理
            recentMessages.forEach { entity ->
                val msg = Message(entity.role, entity.content)
                conversationHistory.add(msg)
                contextManager.addMessage(msg)
            }

            // 渲染到UI
            val chatMessages = recentMessages.map { entity ->
                ChatMessage(entity.role, entity.content)
            }
            _uiState.value = _uiState.value.copy(
                messages = chatMessages
            )
        }
    }

    /**
     * 清空对话
     */
    fun clearConversation() {
        conversationHistory.clear()
        _uiState.value = ChatUiState()
        systemPromptLoaded = false
        currentConversationId = null
        contextManager.clearContext()
    }
}