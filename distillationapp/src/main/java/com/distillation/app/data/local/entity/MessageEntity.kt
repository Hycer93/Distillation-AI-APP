package com.distillation.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 消息实体
 * M2: 完整对话记录
 */
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val conversationId: String,
    val role: String,          // "user" | "assistant" | "system"
    val content: String,
    val timestamp: Long,
    val isEpisodic: Boolean = false  // 标记为情景记忆
)