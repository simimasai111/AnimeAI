package com.animeai.app.network

import com.animeai.app.data.model.GeminiResponse
import retrofit2.Response
import retrofit2.http.*

interface GeminiApi {
    @POST("models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GeminiGenerateRequest
    ): Response<GeminiResponse>
}

data class GeminiGenerateRequest(
    val contents: List<GeminiContentEntry>,
    val generationConfig: GeminiGenerationConfig? = null
)

data class GeminiContentEntry(
    val parts: List<GeminiPartEntry>,
    val role: String? = null
)

data class GeminiPartEntry(
    val text: String? = null
)

data class GeminiGenerationConfig(
    val temperature: Float? = null,
    val maxOutputTokens: Int? = null
)
