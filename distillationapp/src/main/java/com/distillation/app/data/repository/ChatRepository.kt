package com.distillation.app.data.repository

import com.distillation.app.data.local.dao.ConversationDao
import com.distillation.app.data.local.dao.MessageDao
import com.distillation.app.data.local.dao.FlashMemoryDao
import com.distillation.app.data.local.entity.ConversationEntity
import com.distillation.app.data.local.entity.MessageEntity
import com.distillation.app.data.local.entity.FlashMemoryEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 统一数据仓库
 * M2: 封装所有 DAO 操作，对外提供统一入口
 */
@Singleton
class ChatRepository @Inject constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val flashMemoryDao: FlashMemoryDao,
) {
    // ================= 会话管理 =================
    fun getAllConversations(): Flow<List<ConversationEntity>> =
        conversationDao.getAllConversations()

    suspend fun getConversationById(conversationId: String): ConversationEntity? =
        conversationDao.getConversationById(conversationId)

    suspend fun createConversation(title: String = "新对话"): ConversationEntity {
        val conversation = ConversationEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            createdAt = System.currentTimeMillis()
        )
        conversationDao.insertConversation(conversation)
        return conversation
    }

    suspend fun deleteConversation(conversation: ConversationEntity) =
        conversationDao.deleteConversation(conversation)

    // ================= 消息管理 =================
    fun getMessagesByConversation(conversationId: String): Flow<List<MessageEntity>> =
        messageDao.getMessagesByConversation(conversationId)

    suspend fun getLatestMessages(limit: Int = 20): List<MessageEntity> =
        messageDao.getLatestMessages(limit)

    suspend fun searchMessagesByKeyword(keyword: String, limit: Int = 10): List<MessageEntity> =
        messageDao.searchByKeyword(keyword, limit)

    suspend fun saveMessage(
        conversationId: String,
        role: String,
        content: String
    ): MessageEntity {
        val message = MessageEntity(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            role = role,
            content = content,
            timestamp = System.currentTimeMillis()
        )
        messageDao.insertMessage(message)
        return message
    }

    suspend fun saveMessages(messages: List<MessageEntity>) =
        messageDao.insertMessages(messages)

    suspend fun markAsEpisodic(messageId: String) =
        messageDao.markAsEpisodic(messageId)

    suspend fun cleanupOldMessages(beforeTimestamp: Long): Int =
        messageDao.deleteMessagesBefore(beforeTimestamp)

    suspend fun getMessageCount(): Int =
        messageDao.getMessageCount()

    /**
     * 🆕 获取指定会话的最近N条消息
     */
    /**
     * 获取最近一次会话
     */
    suspend fun getLatestConversation(): ConversationEntity? =
        conversationDao.getLatestConversation()

    suspend fun getRecentMessages(conversationId: String, limit: Int = 20): List<MessageEntity> =
        messageDao.getRecentMessages(conversationId, limit)

    // ================= 闪念记忆管理 =================
    fun getAllFlashMemories(): Flow<List<FlashMemoryEntity>> =
        flashMemoryDao.getAllFlashMemories()

    suspend fun getLatestFlashMemories(limit: Int = 20): List<FlashMemoryEntity> =
        flashMemoryDao.getLatest(limit)

    suspend fun getFlashMemoriesByCategory(category: String): List<FlashMemoryEntity> =
        flashMemoryDao.getByCategory(category)

    suspend fun saveFlashMemory(
        content: String,
        category: String,
        source: String = ""
    ): FlashMemoryEntity {
        val memory = FlashMemoryEntity(
            id = UUID.randomUUID().toString(),
            content = content,
            category = category,
            source = source,
            createdAt = System.currentTimeMillis()
        )
        flashMemoryDao.insertFlashMemory(memory)
        return memory
    }

    suspend fun deleteFlashMemory(memory: FlashMemoryEntity) =
        flashMemoryDao.deleteFlashMemory(memory)

    suspend fun deleteFlashMemoryById(id: String) =
        flashMemoryDao.deleteById(id)

    suspend fun getFlashMemoryCount(): Int =
        flashMemoryDao.getCount()
}