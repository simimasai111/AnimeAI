package com.animeai.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

enum class MessageRole {
    @SerializedName("user") USER,
    @SerializedName("assistant") ASSISTANT,
    @SerializedName("system") SYSTEM
}

enum class MessageContentType {
    TEXT,
    IMAGE,
    VIDEO,
    THINKING
}

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = Conversation::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("conversationId")]
)
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val conversationId: String,
    val role: MessageRole,
    val content: String,
    val contentType: MessageContentType = MessageContentType.TEXT,
    val thinkingContent: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val modelName: String? = null,
    val tokenCount: Int? = null
) {
    fun toApiMessage(): ApiMessage = ApiMessage(
        role = when (role) {
            MessageRole.USER -> "user"
            MessageRole.ASSISTANT -> "assistant"
            MessageRole.SYSTEM -> "system"
        },
        content = content
    )
}

data class ApiMessage(
    val role: String,
    val content: String
)

data class ApiChoice(
    val index: Int,
    val message: ApiMessage,
    val delta: ApiMessage? = null,
    val finish_reason: String? = null
)

data class ApiUsage(
    val prompt_tokens: Int? = null,
    val completion_tokens: Int? = null,
    val total_tokens: Int? = null
)

data class ApiResponse(
    val id: String? = null,
    val choices: List<ApiChoice>? = null,
    val usage: ApiUsage? = null,
    val created: Long? = null,
    val model: String? = null
)

data class ApiStreamChunk(
    val id: String? = null,
    val choices: List<ApiChoice>? = null,
    val usage: ApiUsage? = null
)

data class AnthropicMessage(
    val role: String,
    val content: List<AnthropicContent>
)

data class AnthropicContent(
    val type: String,
    val text: String? = null
)

data class AnthropicResponse(
    val id: String? = null,
    val content: List<AnthropicContent>? = null,
    val model: String? = null,
    val usage: ApiUsage? = null
)

data class GeminiContent(
    val parts: List<GeminiPart>? = null,
    val role: String? = null
)

data class GeminiPart(
    val text: String? = null
)

data class GeminiCandidate(
    val content: GeminiContent? = null,
    val finishReason: String? = null
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

data class ImageGenerationRequest(
    val prompt: String,
    val n: Int = 1,
    val size: String = "1024x1024",
    val model: String = "dall-e-3"
)

data class ImageGenerationResponse(
    val data: List<ImageData>? = null
)

data class ImageData(
    val url: String? = null,
    val b64_json: String? = null
)

data class VideoGenerationRequest(
    val prompt: String,
    val model: String = "",
    val duration: Int = 5
)

data class VideoGenerationResponse(
    val id: String? = null,
    val status: String? = null,
    val output: String? = null
)
