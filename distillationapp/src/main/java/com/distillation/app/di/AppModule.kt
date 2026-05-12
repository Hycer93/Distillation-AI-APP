package com.distillation.app.di

import android.content.Context
import androidx.room.Room
import com.distillation.app.data.api.BailianProvider
import com.distillation.app.data.api.ILLMProvider
import com.distillation.app.data.local.AppDatabase
import com.distillation.app.data.local.dao.ConversationDao
import com.distillation.app.data.local.dao.MessageDao
import com.distillation.app.data.local.dao.FlashMemoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt依赖注入模块
 * M1: 提供ILLMProvider的单例注入
 * M2: 新增Room数据库及所有DAO的注入
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ================= API层（M1已有） =================
    @Provides
    @Singleton
    fun provideLLMProvider(provider: BailianProvider): ILLMProvider = provider

    // ================= Room数据库（M2新增） =================
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "distillation_app_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideConversationDao(db: AppDatabase): ConversationDao = db.conversationDao()

    @Provides
    @Singleton
    fun provideMessageDao(db: AppDatabase): MessageDao = db.messageDao()

    @Provides
    @Singleton
    fun provideFlashMemoryDao(db: AppDatabase): FlashMemoryDao = db.flashMemoryDao()

}