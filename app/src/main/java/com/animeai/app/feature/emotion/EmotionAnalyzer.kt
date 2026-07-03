package com.animeai.app.feature.emotion

import com.animeai.app.data.model.ChatMessage
import com.animeai.app.data.model.MessageRole

object EmotionAnalyzer {
    private val positiveWords = setOf("开心", "高兴", "喜欢", "爱", "棒", "好", "优秀", "厉害", "感谢", "谢谢", "nice", "great", "amazing", "wonderful", "beautiful", "happy", "love", "perfect", "excellent")
    private val negativeWords = setOf("难过", "伤心", "生气", "愤怒", "讨厌", "恨", "差", "糟糕", "坏", "烦", "sad", "angry", "bad", "terrible", "awful", "hate", "ugly")
    private val highEnergyWords = setOf("哇", "天", "真的", "太", "超级", "绝对", "必须", "wow", "amazing", "incredible", "!!!", "！", "绝对", "完全")
    private val questionWords = setOf("?", "？", "为什么", "怎么", "什么", "如何", "where", "what", "how", "why", "when")

    fun analyze(text: String, index: Int, timestamp: Long): EmotionPoint {
        val lower = text.lowercase()
        val positiveCount = positiveWords.count { lower.contains(it) }
        val negativeCount = negativeWords.count { lower.contains(it) }
        val highEnergyCount = highEnergyWords.count { lower.contains(it) }
        val totalWords = text.split(Regex("\\s+")).size.coerceAtLeast(1)

        val positiveScore = (positiveCount.toFloat() / totalWords * 10).coerceIn(0f, 1f)
        val negativeScore = (negativeCount.toFloat() / totalWords * 10).coerceIn(0f, 1f)
        val energyLevel = (highEnergyCount.toFloat() / totalWords * 10).coerceIn(0f, 1f)

        val emotion = when {
            positiveScore > 0.3f && energyLevel > 0.3f -> "兴奋"
            positiveScore > 0.3f -> "开心"
            negativeScore > 0.3f && energyLevel > 0.3f -> "愤怒"
            negativeScore > 0.3f -> "难过"
            energyLevel < 0.1f && totalWords < 5 -> "厌倦"
            lower.any { it in "？?" } -> "疑惑"
            else -> "平静"
        }

        val keywords = buildList {
            val sentences = text.split(Regex("[。！？.!?\n]"))
            sentences.filter { it.length in 2..20 }.forEach { add(it.trim()) }
        }.take(3)

        return EmotionPoint(
            messageIndex = index,
            timestamp = timestamp,
            text = text.take(50),
            positiveScore = positiveScore,
            negativeScore = negativeScore,
            energyLevel = energyLevel,
            dominantEmotion = emotion,
            keywords = keywords
        )
    }

    fun analyzeConversation(messages: List<ChatMessage>): EmotionAnalysis {
        val points = messages
            .filter { it.role == MessageRole.USER || it.role == MessageRole.ASSISTANT }
            .mapIndexed { index, msg -> analyze(msg.content, index, msg.timestamp) }

        if (points.isEmpty()) return EmotionAnalysis(emptyList(), 0f, 0f, emptyList(), 0)

        val avgPositive = points.map { it.positiveScore }.average().toFloat()
        val avgNegative = points.map { it.negativeScore }.average().toFloat()
        val overallSentiment = (avgPositive - avgNegative + 1f) / 2f
        val emotionalRange = points.maxOf { it.positiveScore + it.negativeScore } - points.minOf { it.positiveScore + it.negativeScore }
        val dominantEmotions = points.groupBy { it.dominantEmotion }
            .mapValues { it.value.size }
            .entries.sortedByDescending { it.value }
            .take(3)
            .map { it.key }
        val energyPeak = points.indexOfFirst { it.energyLevel == points.maxOf { p -> p.energyLevel } }

        return EmotionAnalysis(
            points = points,
            overallSentiment = overallSentiment,
            emotionalRange = emotionalRange,
            dominantEmotions = dominantEmotions,
            energyPeak = energyPeak
        )
    }
}
