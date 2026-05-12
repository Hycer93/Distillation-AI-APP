package com.distillation.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 会话实体
 * M2: 会话列表
 */
@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val createdAt: Long
)