package com.distillation.app.data.local.dao

import androidx.room.*
import com.distillation.app.data.local.entity.ConversationEntity
import kotlinx.coroutines.flow.Flow

/**
 * 会话 DAO
 * M2: 会话增删改查
 */
@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations ORDER BY createdAt DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :conversationId")
    suspend fun getConversationById(conversationId: String): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Delete
    suspend fun deleteConversation(conversation: ConversationEntity)

    @Query("SELECT * FROM conversations ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestConversation(): ConversationEntity?

    @Query("SELECT COUNT(*) FROM conversations")
    suspend fun getConversationCount(): Int
}