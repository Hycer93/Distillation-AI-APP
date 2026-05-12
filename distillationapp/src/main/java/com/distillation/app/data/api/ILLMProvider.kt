package com.distillation.app.data.api

/**
 * LLM Provider 抽象接口
 * M1: 定义统一的LLM调用接口，便于后续扩展多个Provider
 */
interface ILLMProvider {
    val name: String
    val isAvailable: Boolean
    suspend fun chat(messages: List<Message>): String
}