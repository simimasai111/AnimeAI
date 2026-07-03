package com.animeai.app.network

import com.animeai.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface OpenAIApi {
    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Body request: OpenAICompletionRequest
    ): Response<ApiResponse>

    @POST("chat/completions")
    suspend fun createChatCompletionStream(
        @Body request: OpenAICompletionRequest
    ): Response<okhttp3.ResponseBody>

    @POST("images/generations")
    suspend fun createImage(
        @Body request: ImageGenerationRequest
    ): Response<ImageGenerationResponse>

    @POST("video/generations")
    suspend fun createVideo(
        @Body request: VideoGenerationRequest
    ): Response<VideoGenerationResponse>

    @GET("models")
    suspend fun listModels(): Response<ModelListResponse>
}

data class OpenAICompletionRequest(
    val model: String,
    val messages: List<ApiMessage>,
    val max_tokens: Int = 4096,
    val temperature: Float = 0.7f,
    val stream: Boolean = false,
    val reasoning_effort: String? = null
)

data class ModelListResponse(
    val data: List<ModelInfo> = emptyList()
)

data class ModelInfo(
    val id: String = "",
    val owned_by: String = ""
)
