package com.animeai.app.service

import com.animeai.app.data.model.ChatMessage
import com.animeai.app.data.model.Conversation
import com.animeai.app.data.model.MessageRole
import com.animeai.app.util.TokenUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ConversationMemory(
    val conversationId: String,
    val summary: String = "",
    val totalTokens: Int = 0,
    val messageCount: Int = 0,
    val keyPoints: List<String> = emptyList(),
    val lastInteractionTime: Long = System.currentTimeMillis(),
    val contextInfo: TokenUtils.ContextInfo = TokenUtils.ContextInfo(0, false, false, 0, 0f)
)

data class ConversationGroup(
    val name: String,
    val conversationIds: List<String>
)

object ConversationMemoryService {
    private val _memories = MutableStateFlow<Map<String, ConversationMemory>>(emptyMap())
    val memories: StateFlow<Map<String, ConversationMemory>> = _memories.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    fun updateMemory(conversationId: String, messages: List<ChatMessage>) {
        val contentList = messages.map { it.content }
        val contextInfo = TokenUtils.analyzeContext(contentList)
        val summary = if (contextInfo.shouldSummarize) {
            TokenUtils.generateConversationSummary(contentList)
        } else ""

        val keyPoints = extractKeyPoints(messages)
        val totalTokens = contentList.sumOf { TokenUtils.estimateTokenCount(it) }

        val memory = ConversationMemory(
            conversationId = conversationId,
            summary = summary,
            totalTokens = totalTokens,
            messageCount = messages.size,
            keyPoints = keyPoints,
            lastInteractionTime = messages.lastOrNull()?.timestamp ?: System.currentTimeMillis(),
            contextInfo = contextInfo
        )

        _memories.value = _memories.value + (conversationId to memory)
    }

    fun getMemory(conversationId: String): ConversationMemory? {
        return _memories.value[conversationId]
    }

    fun getContextSummary(conversationId: String): String {
        val memory = _memories.value[conversationId] ?: return ""
        val ctx = memory.contextInfo
        return buildString {
            append("📊 ${ctx.usedPercentage.toInt()}% 上下文使用")
            if (memory.summary.isNotEmpty()) {
                append(" · 已自动摘要")
            }
            if (memory.keyPoints.isNotEmpty()) {
                append(" · ${memory.keyPoints.size} 个关键点")
            }
            append(" · ${memory.messageCount} 条消息")
        }
    }

    fun addSearchQuery(query: String) {
        val history = _searchHistory.value.toMutableList()
        history.remove(query)
        history.add(0, query)
        if (history.size > 10) {
            history.removeAt(history.lastIndex)
        }
        _searchHistory.value = history
    }

    fun getSmartTitle(messages: List<ChatMessage>): String {
        val firstUserMsg = messages.firstOrNull { it.role == MessageRole.USER }?.content ?: return "新对话"
        val cleaned = firstUserMsg
            .replace(Regex("#+\\s*"), "")
            .replace(Regex("\\*+"), "")
            .replace(Regex("\\n+"), " ")
            .trim()
        return if (cleaned.length > 30) cleaned.take(30) + "…" else cleaned
    }

    fun sortConversations(conversations: List<Conversation>, messagesMap: Map<String, List<ChatMessage>>): List<Conversation> {
        return conversations.sortedWith(compareByDescending<Conversation> { it.isPinned }.thenByDescending { it.updatedAt })
    }

    private fun extractKeyPoints(messages: List<ChatMessage>, maxPoints: Int = 5): List<String> {
        val points = mutableListOf<String>()
        val assistantMessages = messages.filter { it.role == MessageRole.ASSISTANT }

        for (msg in assistantMessages.takeLast(10)) {
            val content = msg.content
            val bulletPoints = Regex("""[-*]\s*(.+?)(?:\n|$)""").findAll(content)
            for (match in bulletPoints.take(maxPoints - points.size)) {
                points.add(match.groupValues[1].trim())
            }
            if (points.size >= maxPoints) break
        }

        return points
    }

    fun searchConversations(
        conversations: List<Conversation>,
        messagesMap: Map<String, List<ChatMessage>>,
        query: String
    ): List<Conversation> {
        if (query.isBlank()) return conversations
        val lowerQuery = query.lowercase()

        return conversations.filter { conv ->
            conv.title.lowercase().contains(lowerQuery) ||
            messagesMap[conv.id]?.any { msg ->
                msg.content.lowercase().contains(lowerQuery)
            } == true
        }
    }
}
