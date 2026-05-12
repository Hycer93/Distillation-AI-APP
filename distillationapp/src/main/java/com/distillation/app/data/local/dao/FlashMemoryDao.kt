package com.distillation.app.data.local.dao

import androidx.room.*
import com.distillation.app.data.local.entity.FlashMemoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * 闪念记忆 DAO
 * M2: 闪念记忆增删改查，支持分类检索
 */
@Dao
interface FlashMemoryDao {
    @Query("SELECT * FROM flash_memories ORDER BY createdAt DESC")
    fun getAllFlashMemories(): Flow<List<FlashMemoryEntity>>

    @Query("SELECT * FROM flash_memories ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getLatest(limit: Int = 20): List<FlashMemoryEntity>

    @Query("SELECT * FROM flash_memories WHERE category = :category ORDER BY createdAt DESC")
    suspend fun getByCategory(category: String): List<FlashMemoryEntity>

    @Query("SELECT * FROM flash_memories WHERE id = :id")
    suspend fun getById(id: String): FlashMemoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashMemory(memory: FlashMemoryEntity)

    @Delete
    suspend fun deleteFlashMemory(memory: FlashMemoryEntity)

    @Query("DELETE FROM flash_memories WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM flash_memories")
    suspend fun getCount(): Int

    @Query("SELECT * FROM flash_memories ORDER BY createdAt ASC LIMIT 1")
    suspend fun getOldest(): FlashMemoryEntity?
}