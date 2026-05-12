package com.distillation.app.data.persona

import com.distillation.app.data.api.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 上下文管理器 — Token估算 + 滑动窗口裁剪
 * M4: 维护消息队列，超限自动裁剪最早消息，输出裁剪日志
 */
@Singleton
class ContextManager @Inject constructor() {

    companion object {
        const val MAX_TOKENS = 800_000
        const val CHARS_PER_TOKEN = 0.4
    }

    private val messageQueue = ArrayDeque<Message>()
    private var currentTokens: Int = 0
    private var trimCount: Int = 0

    private val _trimLog = MutableStateFlow<List<TrimLogEntry>>(emptyList())
    val trimLog: StateFlow<List<TrimLogEntry>> = _trimLog.asStateFlow()

    data class TrimLogEntry(
        val timestamp: Long,
        val trimmedTokens: Int,
        val remainingMessages: Int,
        val currentTokens: Int
    )

    data class ContextStats(
        val messageCount: Int,
        val currentTokens: Int,
        val maxTokens: Int,
        val usagePercent: Int,
        val trimCount: Int
    )

    fun addMessage(message: Message) {
        val tokens = calculateTokens(message.content)
        messageQueue.addLast(message)
        currentTokens += tokens

        while (currentTokens > MAX_TOKENS && messageQueue.size > 1) {
            val removed = messageQueue.removeFirst()
            val removedTokens = calculateTokens(removed.content)
            currentTokens -= removedTokens
            trimCount++

            val entry = TrimLogEntry(
                timestamp = System.currentTimeMillis(),
                trimmedTokens = removedTokens,
                remainingMessages = messageQueue.size,
                currentTokens = currentTokens
            )
            _trimLog.value = _trimLog.value + entry
        }
    }

    fun getCurrentContext(): List<Message> = messageQueue.toList()
    fun getCurrentTokenCount(): Int = currentTokens
    fun getTrimCount(): Int = trimCount

    fun calculateTokens(text: String): Int =
        (text.length * CHARS_PER_TOKEN).toInt().coerceAtLeast(1)

    fun clearContext() {
        messageQueue.clear()
        currentTokens = 0
        trimCount = 0
        _trimLog.value = emptyList()
    }

    fun getContextStats(): ContextStats = ContextStats(
        messageCount = messageQueue.size,
        currentTokens = currentTokens,
        maxTokens = MAX_TOKENS,
        usagePercent = (currentTokens.toDouble() / MAX_TOKENS * 100).toInt(),
        trimCount = trimCount
    )
}