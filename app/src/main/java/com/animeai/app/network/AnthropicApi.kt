package com.animeai.app.network

import com.animeai.app.data.model.AnthropicResponse
import com.animeai.app.data.model.ApiMessage
import retrofit2.Response
import retrofit2.http.*

interface AnthropicApi {
    @POST("messages")
    suspend fun createMessage(
        @Header("x-api-key") apiKey: String,
        @Header("anthropic-version") version: String = "2023-06-01",
        @Body request: AnthropicMessageRequest
    ): Response<AnthropicResponse>
}

data class AnthropicMessageRequest(
    val model: String,
    val max_tokens: Int = 4096,
    val messages: List<ApiMessage>,
    val system: String? = null,
    val thinking: AnthropicThinkingConfig? = null
)

data class AnthropicThinkingConfig(
    val type: String = "enabled",
    val budget_tokens: Int = 2048
)
