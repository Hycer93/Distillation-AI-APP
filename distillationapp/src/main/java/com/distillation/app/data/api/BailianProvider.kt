package com.distillation.app.data.api

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BailianProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : ILLMProvider {

    companion object {
        private const val BAILIAN_TEST_API_KEY = "sk-"
    }

    override val name = "阿里云百炼"
    override var isAvailable = true

    private val api: BailianApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            // 暂时改为 BODY 级别，方便看到底发送了什么请求
            level = HttpLoggingInterceptor.Level.BODY
        }

        // ✅ 彻底清理：只保留最干净的网络请求配置，不做任何 SSL 干预
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()

        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        Retrofit.Builder()
            .baseUrl("https://dashscope.aliyuncs.com/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(BailianApi::class.java)
    }

    private fun getApiKey(): String {
        return BAILIAN_TEST_API_KEY
    }

    override suspend fun chat(messages: List<Message>): String {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            throw IllegalStateException("API Key未配置，请在设置页输入阿里云百炼API Key")
        }
        val response = api.chat(
            auth = "Bearer $apiKey",
            request = ChatRequest(
                model = "deepseek-v4-flash",
                messages = messages,
                temperature = 0.7,
                maxTokens = 8192
            )
        )
        return response.choices.firstOrNull()?.message?.content
            ?: throw Exception("API返回为空")
    }
}