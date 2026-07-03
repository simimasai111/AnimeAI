package com.animeai.app.data.model

import com.google.gson.annotations.SerializedName

enum class ApiType(val displayName: String) {
    @SerializedName("openai") OPENAI("OpenAI 兼容"),
    @SerializedName("anthropic") ANTHROPIC("Anthropic 兼容"),
    @SerializedName("gemini") GEMINI("Gemini 兼容")
}

data class ModelConfig(
    val id: String = "default",
    val name: String = "默认配置",
    val apiType: ApiType = ApiType.OPENAI,
    val baseUrl: String = "https://api.openai.com/v1",
    val apiKey: String = "",
    val modelId: String = "gpt-4o",
    val maxTokens: Int = 4096,
    val temperature: Float = 0.7f,
    val enableThinking: Boolean = false,
    val thinkingStrength: ThinkingStrength = ThinkingStrength.MEDIUM,
    val imageModelId: String = "dall-e-3",
    val videoModelId: String = "",
    val isCustom: Boolean = false
)

enum class ThinkingStrength(val label: String, val value: Int) {
    NONE("关闭", 0),
    LOW("低", 25),
    MEDIUM("中", 50),
    HIGH("高", 75),
    MAXIMUM("最高", 100)
}

object PresetConfigs {
    val presets = listOf(
        ModelConfig(
            id = "openai",
            name = "OpenAI",
            apiType = ApiType.OPENAI,
            baseUrl = "https://api.openai.com/v1",
            modelId = "gpt-4o",
            imageModelId = "dall-e-3"
        ),
        ModelConfig(
            id = "deepseek",
            name = "DeepSeek",
            apiType = ApiType.OPENAI,
            baseUrl = "https://api.deepseek.com",
            modelId = "deepseek-chat",
            enableThinking = true,
            thinkingStrength = ThinkingStrength.HIGH
        ),
        ModelConfig(
            id = "claude",
            name = "Claude",
            apiType = ApiType.ANTHROPIC,
            baseUrl = "https://api.anthropic.com",
            modelId = "claude-3-5-sonnet-20241022",
            enableThinking = true
        ),
        ModelConfig(
            id = "gemini",
            name = "Gemini",
            apiType = ApiType.GEMINI,
            baseUrl = "https://generativelanguage.googleapis.com/v1beta",
            modelId = "gemini-2.0-flash-exp"
        ),
        ModelConfig(
            id = "siliconflow",
            name = "SiliconFlow",
            apiType = ApiType.OPENAI,
            baseUrl = "https://api.siliconflow.cn/v1",
            modelId = "deepseek-ai/DeepSeek-V2.5",
            imageModelId = "black-forest-labs/FLUX.1-dev"
        )
    )
}
