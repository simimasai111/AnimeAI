package com.animeai.app.util

object TokenUtils {
    private const val AVG_CHARS_PER_TOKEN = 4.0
    private const val MAX_CONTEXT_TOKENS = 128_000
    private const val MAX_OUTPUT_TOKENS = 4096
    private const val RESERVED_TOKENS = 2048
    private const val SUMMARY_TRIGGER_TOKENS = 100_000
    private const val MEMORY_SUMMARY_TOKENS = 500

    fun estimateTokenCount(text: String): Int {
        if (text.isBlank()) return 0
        val charCount = text.length.toDouble()
        val cjkCount = text.count { it in '\u4E00'..'\u9FFF' || it in '\u3000'..'\u303F' || it in '\uFF00'..'\uFFEF' }
        val englishChars = charCount - cjkCount
        val cjkTokens = cjkCount * 1.5
        val englishTokens = englishChars / AVG_CHARS_PER_TOKEN
        return (cjkTokens + englishTokens).toInt().coerceAtLeast(1)
    }

    fun estimateMessagesTokenCount(messages: List<String>): Int {
        return messages.sumOf { estimateTokenCount(it) }
    }

    data class ContextInfo(
        val totalTokens: Int,
        val isNearLimit: Boolean,
        val shouldSummarize: Boolean,
        val availableTokens: Int,
        val usedPercentage: Float
    )

    fun analyzeContext(messages: List<String>): ContextInfo {
        val totalTokens = estimateMessagesTokenCount(messages)
        val availableTokens = MAX_CONTEXT_TOKENS - MAX_OUTPUT_TOKENS - RESERVED_TOKENS
        val usedPercentage = (totalTokens.toFloat() / availableTokens * 100).coerceAtMost(100f)

        return ContextInfo(
            totalTokens = totalTokens,
            isNearLimit = totalTokens > SUMMARY_TRIGGER_TOKENS,
            shouldSummarize = totalTokens > SUMMARY_TRIGGER_TOKENS,
            availableTokens = availableTokens,
            usedPercentage = usedPercentage
        )
    }

    fun generateConversationSummary(messages: List<String>, maxLength: Int = MEMORY_SUMMARY_TOKENS): String {
        if (messages.isEmpty()) return ""

        val totalTokens = estimateMessagesTokenCount(messages)
        if (totalTokens <= maxLength) return messages.joinToString("\n")

        val firstMessages = mutableListOf<String>()
        val lastMessages = mutableListOf<String>()
        var firstTokens = 0
        var lastTokens = 0
        val halfBudget = maxLength / 2

        for (msg in messages) {
            val tokens = estimateTokenCount(msg)
            if (firstTokens + tokens <= halfBudget) {
                firstMessages.add(msg)
                firstTokens += tokens
            } else break
        }

        for (msg in messages.reversed()) {
            val tokens = estimateTokenCount(msg)
            if (lastTokens + tokens <= halfBudget) {
                lastMessages.add(0, msg)
                lastTokens += tokens
            } else break
        }

        return buildString {
            if (firstMessages.isNotEmpty()) {
                append(firstMessages.joinToString("\n"))
                append("\n\n... [中间省略 ${totalTokens - firstTokens - lastTokens} tokens] ...\n\n")
            }
            if (lastMessages.isNotEmpty()) {
                append(lastMessages.joinToString("\n"))
            }
        }
    }

    fun getContextUsageString(totalTokens: Int): String {
        val percentage = (totalTokens.toFloat() / MAX_CONTEXT_TOKENS * 100).coerceAtMost(100f)
        return when {
            percentage < 25 -> "上下文使用: ${percentage.toInt()}% (余量充足)"
            percentage < 50 -> "上下文使用: ${percentage.toInt()}% (正常使用)"
            percentage < 75 -> "上下文使用: ${percentage.toInt()}% (注意积累)"
            percentage < 90 -> "上下文使用: ${percentage.toInt()}% (接近上限)"
            else -> "上下文使用: ${percentage.toInt()}% (请考虑新对话)"
        }
    }

    fun truncateToFit(text: String, maxTokens: Int): String {
        val estimated = estimateTokenCount(text)
        if (estimated <= maxTokens) return text
        val ratio = maxTokens.toFloat() / estimated
        val maxChars = (text.length * ratio).toInt()
        return text.take(maxChars) + "\n\n[内容已截断，原文本约 $estimated tokens]"
    }
}
