package com.distillation.app.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val model: String = "deepseek-chat",
    val messages: List<Message>,
    val temperature: Double = 0.7,
    @SerialName("max_tokens")
    val maxTokens: Int = 8192
)

@Serializable
data class Message(
    val role: String,    // "system" | "user" | "assistant"
    val content: String
)

@Serializable
data class ChatResponse(
    val id: String,
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: Message,
    @SerialName("finish_reason")
    val finishReason: String
)