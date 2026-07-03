package com.animeai.app.feature.emotion

data class EmotionPoint(
    val messageIndex: Int,
    val timestamp: Long,
    val text: String,
    val positiveScore: Float,
    val negativeScore: Float,
    val energyLevel: Float,
    val dominantEmotion: String,
    val keywords: List<String>
)

data class EmotionAnalysis(
    val points: List<EmotionPoint>,
    val overallSentiment: Float,
    val emotionalRange: Float,
    val dominantEmotions: List<String>,
    val energyPeak: Int
)

data class EmotionColor(
    val emotion: String,
    val color: String,
    val emoji: String
)

val emotionColorMap = listOf(
    EmotionColor("开心", "#FFD700", "😊"),
    EmotionColor("兴奋", "#FF6B35", "🤩"),
    EmotionColor("平静", "#87CEEB", "😌"),
    EmotionColor("疑惑", "#9B59B6", "🤔"),
    EmotionColor("惊讶", "#F1C40F", "😮"),
    EmotionColor("难过", "#5DADE2", "😢"),
    EmotionColor("愤怒", "#E74C3C", "😠"),
    EmotionColor("焦虑", "#F39C12", "😰"),
    EmotionColor("厌倦", "#95A5A6", "😐"),
    EmotionColor("喜爱", "#FF69B4", "🥰")
)
