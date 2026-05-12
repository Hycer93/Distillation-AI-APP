package com.distillation.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 闪念记忆实体
 * M2: 精选层，用于记忆复活模块
 */
@Entity(tableName = "flash_memories")
data class FlashMemoryEntity(
    @PrimaryKey
    val id: String,
    val content: String,
    val category: String,      // 策略进化 / 技能淬炼 / 反面教材 / 关系里程碑 / 关键约定
    val source: String,        // 来源对话ID
    val createdAt: Long
)