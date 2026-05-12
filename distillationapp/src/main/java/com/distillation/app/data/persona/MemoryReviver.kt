package com.distillation.app.data.persona

import com.distillation.app.data.local.dao.FlashMemoryDao
import com.distillation.app.data.local.dao.MessageDao
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 记忆复活器
 * M4: 新会话启动时生成记忆摘要注入System Prompt，用户触发时检索历史
 */
@Singleton
class MemoryReviver @Inject constructor(
    private val flashMemoryDao: FlashMemoryDao,
    private val messageDao: MessageDao
) {
    suspend fun generateRevivePrompt(): String {
        val sb = StringBuilder()
        sb.appendLine("【记忆复苏·当前状态】")


        val agreements = flashMemoryDao.getByCategory("关键约定")
        if (agreements.isNotEmpty()) {
            sb.appendLine()
            sb.appendLine("核心共识：")
            agreements.forEach { sb.appendLine("  - ${it.content}") }
        }

        val latestFlash = flashMemoryDao.getLatest(5)
        val actionItems = latestFlash.filter { it.category != "关键约定" }
        if (actionItems.isNotEmpty()) {
            sb.appendLine()
            sb.appendLine("最近关键事件：")
            actionItems.forEach { sb.appendLine("  - [${it.category}] ${it.content}") }
        }

        val recentMessages = messageDao.getLatestMessages(6)
        if (recentMessages.isNotEmpty()) {
            sb.appendLine()
            sb.appendLine("最近对话：")
            recentMessages.forEach { msg ->
                val roleLabel = if (msg.role == "user") "厂长" else "蒸馏仔"
                val preview = if (msg.content.length > 100) msg.content.take(100) + "..." else msg.content
                sb.appendLine("  [$roleLabel] $preview")
            }
        }

        sb.appendLine()
        sb.appendLine("记忆复苏完成，等待厂长指令。")
        return sb.toString()
    }

    suspend fun getFlashMemorySummary(limit: Int = 20): String {
        val memories = flashMemoryDao.getLatest(limit)
        if (memories.isEmpty()) return ""
        val sb = StringBuilder()
        sb.appendLine("【成长记忆·闪念】")
        memories.forEach { memory ->
            sb.appendLine("- [${memory.category}] ${memory.content}")
        }
        return sb.toString()
    }
}