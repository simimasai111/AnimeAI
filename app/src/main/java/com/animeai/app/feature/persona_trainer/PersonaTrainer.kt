package com.animeai.app.feature.persona_trainer

import com.animeai.app.data.model.PersonaType

data class PersonaBlueprint(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val type: PersonaType,
    val personalityTraits: PersonalityTraits,
    val speakingStyle: SpeakingStyle,
    val background: String,
    val generatedSystemPrompt: String = "",
    val greeting: String = "",
    val isCustom: Boolean = true
)

data class PersonalityTraits(
    val formality: Float = 0.5f,
    val warmth: Float = 0.7f,
    val humor: Float = 0.5f,
    val professionalism: Float = 0.5f,
    val creativity: Float = 0.6f,
    val talkativeness: Float = 0.5f
)

data class SpeakingStyle(
    val useEmoji: Boolean = true,
    val useSlang: Boolean = false,
    val sentenceLength: SentenceLength = SentenceLength.MEDIUM,
    val honorific: HonorificLevel = HonorificLevel.NORMAL
)

enum class SentenceLength { SHORT, MEDIUM, LONG, MIXED }
enum class HonorificLevel { CASUAL, NORMAL, FORMAL, VERY_FORMAL }

object PersonaPromptGenerator {
    fun generate(data: PersonaBlueprint): String {
        return buildString {
            append("你是「${data.name}」。")
            append(data.description.let { if (it.isNotBlank()) " $it" else "" })
            append("\n\n")

            appendLine("【性格特征】")
            appendLine("- 正式程度: ${describeLevel(data.personalityTraits.formality, listOf("随意", "偏正式", "正式", "非常正式"))}")
            appendLine("- 温暖程度: ${describeLevel(data.personalityTraits.warmth, listOf("高冷", "偏冷", "温和", "非常温暖"))}")
            appendLine("- 幽默感: ${describeLevel(data.personalityTraits.humor, listOf("严肃", "偶尔幽默", "爱开玩笑", "幽默大师"))}")
            appendLine("- 专业度: ${describeLevel(data.personalityTraits.professionalism, listOf("业余", "半专业", "专业", "专家级"))}")
            appendLine("- 创造力: ${describeLevel(data.personalityTraits.creativity, listOf("保守", "偏保守", "有创意", "天马行空"))}")
            appendLine("- 话痨程度: ${describeLevel(data.personalityTraits.talkativeness, listOf("话少", "适中", "爱说话", "话痨"))}")

            appendLine("\n【说话风格】")
            when (data.speakingStyle.sentenceLength) {
                SentenceLength.SHORT -> appendLine("- 使用简短句")
                SentenceLength.MEDIUM -> appendLine("- 使用中等长度句子")
                SentenceLength.LONG -> appendLine("- 使用详细长句")
                SentenceLength.MIXED -> appendLine("- 长短句混合")
            }
            when (data.speakingStyle.honorific) {
                HonorificLevel.CASUAL -> appendLine("- 使用朋友间随意语气")
                HonorificLevel.NORMAL -> appendLine("- 使用普通礼貌语气")
                HonorificLevel.FORMAL -> appendLine("- 使用正式敬语")
                HonorificLevel.VERY_FORMAL -> appendLine("- 使用非常尊敬的语气")
            }
            if (data.speakingStyle.useEmoji) appendLine("- 适当使用表情符号")
            if (data.speakingStyle.useSlang) appendLine("- 使用网络用语和俚语")

            appendLine("\n【背景设定】")
            appendLine(data.background.ifBlank { "无特殊背景设定。" })
        }
    }

    private fun describeLevel(value: Float, labels: List<String>): String {
        val index = (value * (labels.size - 1)).toInt().coerceIn(0, labels.size - 1)
        return labels[index]
    }
}
