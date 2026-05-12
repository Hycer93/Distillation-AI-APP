package com.distillation.app.data.local.dao

import androidx.room.*
import com.distillation.app.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

/**
 * 消息 DAO
 * M2: 消息记录增删改查，支持关键词检索和情景记忆标记
 */
@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesByConversation(conversationId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getLatestMessages(limit: Int): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE content LIKE '%' || :keyword || '%' ORDER BY timestamp DESC LIMIT :limit")
    suspend fun searchByKeyword(keyword: String, limit: Int = 10): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Update
    suspend fun updateMessage(message: MessageEntity)

    @Query("UPDATE messages SET isEpisodic = :isEpisodic WHERE id = :messageId")
    suspend fun markAsEpisodic(messageId: String, isEpisodic: Boolean = true)

    @Query("DELETE FROM messages WHERE timestamp < :beforeTimestamp")
    suspend fun deleteMessagesBefore(beforeTimestamp: Long): Int

    @Query("SELECT COUNT(*) FROM messages")
    suspend fun getMessageCount(): Int

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC LIMIT :limit")
    suspend fun getRecentMessages(conversationId: String, limit: Int): List<MessageEntity>
}