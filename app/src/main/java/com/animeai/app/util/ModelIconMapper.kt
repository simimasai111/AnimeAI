package com.animeai.app.util

import androidx.compose.ui.graphics.Color
import com.animeai.app.R

data class ModelIconInfo(
    val drawableResId: Int,
    val backgroundColor: Color,
    val accentColor: Color,
    val displayName: String = ""
)

object ModelIconMapper {
    private val modelMap = listOf(
        ModelEntry("openai", R.drawable.ic_model_openai, Color(0xFF10A37F), "OpenAI"),
        ModelEntry("gpt-4", R.drawable.ic_model_openai, Color(0xFF10A37F), "OpenAI GPT-4"),
        ModelEntry("gpt-3", R.drawable.ic_model_openai, Color(0xFF10A37F), "OpenAI GPT"),
        ModelEntry("o1-", R.drawable.ic_model_openai, Color(0xFF10A37F), "OpenAI o1"),
        ModelEntry("o3-", R.drawable.ic_model_openai, Color(0xFF10A37F), "OpenAI o3"),
        ModelEntry("dall-e", R.drawable.ic_model_dalle, Color(0xFF7B2D8E), "DALL-E"),

        ModelEntry("claude", R.drawable.ic_model_anthropic, Color(0xFF6B4F8F), "Claude"),
        ModelEntry("anthropic", R.drawable.ic_model_anthropic, Color(0xFF6B4F8F), "Anthropic"),

        ModelEntry("gemini", R.drawable.ic_model_gemini, Color(0xFF4285F4), "Gemini"),
        ModelEntry("gemma", R.drawable.ic_model_gemma, Color(0xFF2196F3), "Gemma"),

        ModelEntry("deepseek", R.drawable.ic_model_deepseek, Color(0xFF1E3A5F), "DeepSeek"),

        ModelEntry("qwen", R.drawable.ic_model_qwen, Color(0xFF8B0000), "Qwen"),
        ModelEntry("qwq", R.drawable.ic_model_qwen, Color(0xFF8B0000), "QwQ"),
        ModelEntry("qwen2", R.drawable.ic_model_qwen, Color(0xFF8B0000), "Qwen2"),

        ModelEntry("mistral", R.drawable.ic_model_mistral, Color(0xFFFF6B35), "Mistral"),
        ModelEntry("mixtral", R.drawable.ic_model_mistral, Color(0xFFFF6B35), "Mixtral"),
        ModelEntry("codestral", R.drawable.ic_model_mistral, Color(0xFFFF6B35), "Codestral"),

        ModelEntry("llama", R.drawable.ic_model_llama, Color(0xFF5B8DEF), "Llama"),
        ModelEntry("llama-3", R.drawable.ic_model_llama, Color(0xFF5B8DEF), "Llama 3"),

        ModelEntry("yi-", R.drawable.ic_model_yi, Color(0xFFFF4D4D), "Yi"),
        ModelEntry("yi_", R.drawable.ic_model_yi, Color(0xFFFF4D4D), "Yi"),

        ModelEntry("hunyuan", R.drawable.ic_model_hunyuan, Color(0xFF0052D9), "混元"),
        ModelEntry("longma", R.drawable.ic_model_longma, Color(0xFFFF8C00), "龙猫"),

        ModelEntry("flux", R.drawable.ic_model_flux, Color(0xFF00BCD4), "FLUX"),
        ModelEntry("stable-diffusion", R.drawable.ic_model_stablediffusion, Color(0xFF8B5CF6), "Stable Diffusion"),
        ModelEntry("sdxl", R.drawable.ic_model_stablediffusion, Color(0xFF8B5CF6), "SDXL"),

        ModelEntry("minimax", R.drawable.ic_model_minimax, Color(0xFF1A1A2E), "MiniMax"),
        ModelEntry("minmax", R.drawable.ic_model_minimax, Color(0xFF1A1A2E), "MiniMax"),

        ModelEntry("moonshot", R.drawable.ic_model_moonshot, Color(0xFF0D0D2B), "Moonshot"),
        ModelEntry("kimi", R.drawable.ic_model_moonshot, Color(0xFF0D0D2B), "Kimi"),

        ModelEntry("baichuan", R.drawable.ic_model_baichuan, Color(0xFF1664FF), "百川"),
        ModelEntry("glm", R.drawable.ic_model_glm, Color(0xFF3366FF), "GLM"),
        ModelEntry("chatglm", R.drawable.ic_model_glm, Color(0xFF3366FF), "ChatGLM"),

        ModelEntry("ernie", R.drawable.ic_model_ernie, Color(0xFF2932E0), "ERNIE"),
        ModelEntry("spark", R.drawable.ic_model_spark, Color(0xFFFF6600), "Spark"),
        ModelEntry("xinghuo", R.drawable.ic_model_spark, Color(0xFFFF6600), "星火"),

        ModelEntry("grok", R.drawable.ic_model_grok, Color(0xFF1C1C1E), "Grok"),
        ModelEntry("phi", R.drawable.ic_model_phi, Color(0xFF0078D4), "Phi"),
        ModelEntry("command-r", R.drawable.ic_model_cohere, Color(0xFF395162), "Command-R"),
        ModelEntry("cohere", R.drawable.ic_model_cohere, Color(0xFF395162), "Cohere"),

        ModelEntry("doubao", R.drawable.ic_model_doubao, Color(0xFFFF6B81), "豆包"),
        ModelEntry("sense", R.drawable.ic_model_sensechat, Color(0xFF6200EA), "SenseChat"),
        ModelEntry("sensechat", R.drawable.ic_model_sensechat, Color(0xFF6200EA), "SenseChat"),

        ModelEntry("groq", R.drawable.ic_model_groq, Color(0xFFF97316), "Groq"),
        ModelEntry("openrouter", R.drawable.ic_model_openrouter, Color(0xFF8020E0), "OpenRouter"),
        ModelEntry("perplexity", R.drawable.ic_model_perplexity, Color(0xFF1F1F1F), "Perplexity"),
        ModelEntry("pplx", R.drawable.ic_model_perplexity, Color(0xFF1F1F1F), "PPLX"),

        ModelEntry("jamba", R.drawable.ic_model_ai21, Color(0xFF1A1A2E), "Jamba"),
        ModelEntry("ai21", R.drawable.ic_model_ai21, Color(0xFF1A1A2E), "AI21"),

        ModelEntry("wizard", R.drawable.ic_model_wizard, Color(0xFF7C3AED), "Wizard"),
        ModelEntry("wizardlm", R.drawable.ic_model_wizard, Color(0xFF7C3AED), "WizardLM"),

        ModelEntry("skywork", R.drawable.ic_model_skywork, Color(0xFF0088FF), "天工"),
        ModelEntry("tiangong", R.drawable.ic_model_tiangong, Color(0xFF1A237E), "天工"),

        ModelEntry("tencent", R.drawable.ic_model_tencent, Color(0xFF0066FF), "Tencent"),
        ModelEntry("tongyi", R.drawable.ic_model_tongyi, Color(0xFF1677FF), "通义"),
        ModelEntry("step-", R.drawable.ic_model_stepfun, Color(0xFF00C853), "Step"),
        ModelEntry("step_", R.drawable.ic_model_stepfun, Color(0xFF00C853), "Step"),

        ModelEntry("snowflake", R.drawable.ic_model_snowflake, Color(0xFF00BCD4), "Snowflake"),
        ModelEntry("arctic", R.drawable.ic_model_snowflake, Color(0xFF00BCD4), "Arctic"),

        ModelEntry("vicuna", R.drawable.ic_model_default, Color(0xFF9850FF), "Vicuna"),
        ModelEntry("solar", R.drawable.ic_model_default, Color(0xFF9850FF), "Solar"),
        ModelEntry("dbrx", R.drawable.ic_model_default, Color(0xFF9850FF), "DBRX"),
        ModelEntry("reka", R.drawable.ic_model_default, Color(0xFF9850FF), "Reka"),
        ModelEntry("sambanova", R.drawable.ic_model_default, Color(0xFF9850FF), "SambaNova"),
        ModelEntry("together", R.drawable.ic_model_default, Color(0xFF9850FF), "Together"),
        ModelEntry("fireworks", R.drawable.ic_model_default, Color(0xFF9850FF), "Fireworks"),
        ModelEntry("siliconflow", R.drawable.ic_model_default, Color(0xFF9850FF), "SiliconFlow"),
        ModelEntry("01.ai", R.drawable.ic_model_default, Color(0xFF9850FF), "01.AI"),
    )

    private val suffixMap = mapOf(
        "instruct" to setOf("llama", "mistral", "qwen", "yi"),
        "chat" to setOf("gpt", "qwen", "glm", "baichuan"),
        "coder" to setOf("deepseek", "qwen", "yi"),
        "vision" to setOf("gpt", "claude", "gemini", "qwen"),
        "turbo" to setOf("gpt"),
        "mini" to setOf("gpt", "gemini", "qwen"),
        "pro" to setOf("gemini", "qwen", "yi", "hunyuan"),
        "ultra" to setOf("claude"),
        "sonnet" to setOf("claude"),
        "opus" to setOf("claude"),
        "haiku" to setOf("claude"),
        "flash" to setOf("gemini"),
        "schnell" to setOf("flux"),
        "dev" to setOf("flux"),
        "1b" to setOf("phi", "gemma", "qwen"),
        "3b" to setOf("phi", "gemma", "qwen"),
        "7b" to setOf("llama", "mistral", "yi", "qwen", "glm"),
        "8b" to setOf("llama", "qwen", "mistral", "gemma"),
        "13b" to setOf("llama", "yi"),
        "20b" to setOf("yi", "qwen"),
        "32b" to setOf("qwen", "yi"),
        "34b" to setOf("yi", "qwen"),
        "70b" to setOf("llama", "qwen"),
        "72b" to setOf("qwen"),
        "110b" to setOf("yi"),
    )

    fun getModelIcon(modelName: String): ModelIconInfo {
        if (modelName.isBlank()) {
            return ModelIconInfo(R.drawable.ic_model_default, Color(0xFF9850FF), Color(0xFFFFFFFF), "AI")
        }

        val lower = modelName.lowercase()

        val exactMatch = modelMap.find { entry ->
            lower == entry.pattern || lower.startsWith("${entry.pattern}/") || lower.startsWith("${entry.pattern}-")
        }
        if (exactMatch != null) {
            return ModelIconInfo(exactMatch.resId, exactMatch.bgColor, Color(0xFFFFFFFF), exactMatch.displayName)
        }

        val patternMatch = modelMap.find { entry ->
            lower.contains(entry.pattern) || 
            lower.startsWith(entry.pattern) ||
            lower.replace("_", "-").contains(entry.pattern.replace("_", "-"))
        }
        if (patternMatch != null) {
            return ModelIconInfo(patternMatch.resId, patternMatch.bgColor, Color(0xFFFFFFFF), patternMatch.displayName)
        }

        for ((suffix, baseSet) in suffixMap) {
            if (lower.endsWith(suffix) || lower.contains("-$suffix") || lower.contains("_$suffix")) {
                for (base in baseSet) {
                    val baseMatch = modelMap.find { it.pattern == base }
                    if (baseMatch != null) {
                        return ModelIconInfo(baseMatch.resId, baseMatch.bgColor, Color(0xFFFFFFFF), baseMatch.displayName)
                    }
                }
            }
        }

        val firstPart = lower.split("/").firstOrNull() ?: lower
        val firstPartMatch = modelMap.find { firstPart.contains(it.pattern) }
        if (firstPartMatch != null) {
            return ModelIconInfo(firstPartMatch.resId, firstPartMatch.bgColor, Color(0xFFFFFFFF), firstPartMatch.displayName)
        }

        return ModelIconInfo(R.drawable.ic_model_default, Color(0xFF9850FF), Color(0xFFFFFFFF), "AI")
    }

    fun getModelColor(modelName: String): Color = getModelIcon(modelName).backgroundColor

    fun getModelDrawable(modelName: String): Int = getModelIcon(modelName).drawableResId
}

private data class ModelEntry(
    val pattern: String,
    val resId: Int,
    val bgColor: Color,
    val displayName: String
)
