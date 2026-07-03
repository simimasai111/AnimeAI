package com.animeai.app.network

import com.animeai.app.data.model.ApiType
import com.animeai.app.data.model.ImageGenerationRequest
import com.animeai.app.data.model.ModelConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .also { builder ->
                val logging = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
                builder.addInterceptor(logging)
            }
            .build()
    }

    private val authClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer ${chain.request().header("X-Api-Key")}")
                    .build()
                chain.proceed(request)
            }
            .also { builder ->
                val logging = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
                builder.addInterceptor(logging)
            }
            .build()
    }

    fun createOpenAIApi(config: ModelConfig): OpenAIApi {
        val baseUrl = if (config.baseUrl.endsWith("/")) config.baseUrl else "$config.baseUrl/"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(authClient.newBuilder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val request = original.newBuilder()
                        .header("Authorization", "Bearer ${config.apiKey}")
                        .build()
                    chain.proceed(request)
                }
                .build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAIApi::class.java)
    }

    fun createAnthropicApi(config: ModelConfig): AnthropicApi {
        val baseUrl = if (config.baseUrl.endsWith("/")) config.baseUrl else "$config.baseUrl/"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(authClient.newBuilder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val request = original.newBuilder()
                        .header("x-api-key", config.apiKey)
                        .header("anthropic-version", "2023-06-01")
                        .build()
                    chain.proceed(request)
                }
                .build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AnthropicApi::class.java)
    }

    fun createGeminiApi(config: ModelConfig): GeminiApi {
        val baseUrl = if (config.baseUrl.endsWith("/")) config.baseUrl else "$config.baseUrl/"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApi::class.java)
    }

    suspend fun sendMessage(
        config: ModelConfig,
        messages: List<com.animeai.app.data.model.ApiMessage>,
        systemPrompt: String? = null,
        stream: Boolean = false
    ): Result<String> {
        return try {
            when (config.apiType) {
                ApiType.OPENAI -> sendOpenAI(config, messages, systemPrompt, stream)
                ApiType.ANTHROPIC -> sendAnthropic(config, messages, systemPrompt)
                ApiType.GEMINI -> sendGemini(config, messages)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun sendOpenAI(
        config: ModelConfig,
        messages: List<com.animeai.app.data.model.ApiMessage>,
        systemPrompt: String?,
        stream: Boolean
    ): Result<String> {
        val api = createOpenAIApi(config)
        val fullMessages = mutableListOf<com.animeai.app.data.model.ApiMessage>()
        systemPrompt?.let {
            fullMessages.add(com.animeai.app.data.model.ApiMessage("system", it))
        }
        fullMessages.addAll(messages)

        val reasoningEffort = if (config.enableThinking) {
            when (config.thinkingStrength) {
                com.animeai.app.data.model.ThinkingStrength.NONE -> null
                com.animeai.app.data.model.ThinkingStrength.LOW -> "low"
                com.animeai.app.data.model.ThinkingStrength.MEDIUM -> "medium"
                com.animeai.app.data.model.ThinkingStrength.HIGH -> "high"
                com.animeai.app.data.model.ThinkingStrength.MAXIMUM -> "high"
            }
        } else null

        val request = OpenAICompletionRequest(
            model = config.modelId,
            messages = fullMessages,
            max_tokens = config.maxTokens,
            temperature = config.temperature,
            stream = stream,
            reasoning_effort = reasoningEffort
        )

        val response = api.createChatCompletion(request)
        if (!response.isSuccessful) {
            return Result.failure(Exception("API Error: ${response.code()} ${response.message()}"))
        }

        val body = response.body()
        val content = body?.choices?.firstOrNull()?.message?.content
            ?: return Result.failure(Exception("Empty response"))
        return Result.success(content)
    }

    private suspend fun sendAnthropic(
        config: ModelConfig,
        messages: List<com.animeai.app.data.model.ApiMessage>,
        systemPrompt: String?
    ): Result<String> {
        val api = createAnthropicApi(config)
        val request = AnthropicMessageRequest(
            model = config.modelId,
            max_tokens = config.maxTokens,
            messages = messages,
            system = systemPrompt,
            thinking = if (config.enableThinking) {
                AnthropicThinkingConfig(budget_tokens = 2048)
            } else null
        )

        val response = api.createMessage(config.apiKey, request = request)
        if (!response.isSuccessful) {
            return Result.failure(Exception("API Error: ${response.code()} ${response.message()}"))
        }

        val body = response.body()
        val content = body?.content?.firstOrNull { it.type == "text" }?.text
            ?: return Result.failure(Exception("Empty response"))
        return Result.success(content)
    }

    private suspend fun sendGemini(
        config: ModelConfig,
        messages: List<com.animeai.app.data.model.ApiMessage>
    ): Result<String> {
        val api = createGeminiApi(config)
        val contents = messages.map { msg ->
            GeminiContentEntry(
                parts = listOf(GeminiPartEntry(text = msg.content)),
                role = when (msg.role) {
                    "user" -> "user"
                    "assistant" -> "model"
                    else -> "user"
                }
            )
        }

        val request = GeminiGenerateRequest(
            contents = contents,
            generationConfig = GeminiGenerationConfig(
                temperature = config.temperature,
                maxOutputTokens = config.maxTokens
            )
        )

        val response = api.generateContent(
            model = config.modelId,
            apiKey = config.apiKey,
            request = request
        )
        if (!response.isSuccessful) {
            return Result.failure(Exception("API Error: ${response.code()} ${response.message()}"))
        }

        val body = response.body()
        val content = body?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: return Result.failure(Exception("Empty response"))
        return Result.success(content)
    }

    suspend fun generateImage(
        config: ModelConfig,
        prompt: String
    ): Result<String> {
        return try {
            when (config.apiType) {
                ApiType.OPENAI -> {
                    val api = createOpenAIApi(config)
                    val request = ImageGenerationRequest(
                        prompt = prompt,
                        model = config.imageModelId.ifEmpty { "dall-e-3" }
                    )
                    val response = api.createImage(request)
                    if (!response.isSuccessful) {
                        return Result.failure(Exception("Image generation failed: ${response.code()}"))
                    }
                    val url = response.body()?.data?.firstOrNull()?.url
                        ?: return Result.failure(Exception("No image URL returned"))
                    Result.success(url)
                }
                else -> Result.failure(Exception("Image generation only supported for OpenAI compatible APIs"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
