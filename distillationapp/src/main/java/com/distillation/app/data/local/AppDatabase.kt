package com.distillation.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.distillation.app.data.local.entity.ConversationEntity
import com.distillation.app.data.local.entity.MessageEntity
import com.distillation.app.data.local.entity.FlashMemoryEntity
import com.distillation.app.data.local.dao.ConversationDao
import com.distillation.app.data.local.dao.MessageDao
import com.distillation.app.data.local.dao.FlashMemoryDao

/**
 * Room 数据库定义
 * M2: 包含所有实体和 DAO
 */
@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        FlashMemoryEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun flashMemoryDao(): FlashMemoryDao
}