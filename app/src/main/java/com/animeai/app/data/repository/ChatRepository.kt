package com.animeai.app.data.repository

import com.animeai.app.data.local.AppDatabase
import com.animeai.app.data.local.ChatDao
import com.animeai.app.data.model.*
import com.animeai.app.network.ApiClient
import com.animeai.app.service.ConversationMemoryService
import com.animeai.app.util.TokenUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

class ChatRepository(private val database: AppDatabase) {
    private val chatDao: ChatDao = database.chatDao()

    fun getAllConversations(): Flow<List<Conversation>> = chatDao.getAllConversations()

    fun getConversation(id: String): Flow<Conversation?> = chatDao.getConversationFlow(id)

    fun getMessages(conversationId: String): Flow<List<ChatMessage>> = chatDao.getMessages(conversationId)

    suspend fun createConversation(
        persona: Persona,
        modelConfigId: String,
        title: String? = null
    ): Conversation {
        val conversation = Conversation(
            id = UUID.randomUUID().toString(),
            title = title ?: "",
            personaId = persona.id,
            modelConfigId = modelConfigId
        )
        chatDao.insertConversation(conversation)
        return conversation
    }

    suspend fun deleteConversation(id: String) {
        chatDao.deleteConversationById(id)
    }

    suspend fun pinConversation(id: String, pinned: Boolean) {
        chatDao.pinConversation(id, pinned)
    }

    suspend fun sendMessage(
        config: ModelConfig,
        conversation: Conversation,
        userMessage: ChatMessage,
        persona: Persona
    ): Result<ChatMessage> {
        val userTokenCount = TokenUtils.estimateTokenCount(userMessage.content)
        chatDao.insertMessage(userMessage.copy(tokenCount = userTokenCount))

        val messages = chatDao.getMessages(conversation.id).first()
        val allContent = messages.map { it.content }
        val contextInfo = TokenUtils.analyzeContext(allContent)

        val apiMessages = if (contextInfo.shouldSummarize) {
            val summary = ConversationMemoryService.getMemory(conversation.id)?.summary
            if (summary != null && summary.isNotEmpty()) {
                val recentMessages = messages.takeLast(20)
                listOf(
                    ApiMessage("system", "以下是对话的历史摘要：\n$summary\n\n请基于以上上下文回答用户最新的问题。"),
                ) + recentMessages.map { it.toApiMessage() }
            } else messages.map { it.toApiMessage() }
        } else messages.map { it.toApiMessage() }

        val result = ApiClient.sendMessage(
            config = config,
            messages = apiMessages,
            systemPrompt = persona.systemPrompt,
            stream = false
        )

        return result.map { content ->
            val assistantTokenCount = TokenUtils.estimateTokenCount(content)
            val assistantMessage = ChatMessage(
                conversationId = conversation.id,
                role = MessageRole.ASSISTANT,
                content = content,
                modelName = config.modelId,
                tokenCount = assistantTokenCount
            )
            chatDao.insertMessage(assistantMessage)

            val firstMsg = chatDao.getFirstMessage(conversation.id)
            val title = if (conversation.title.isNullOrBlank()) {
                ConversationMemoryService.getSmartTitle(messages + userMessage)
            } else conversation.title

            chatDao.updateConversation(
                conversation.copy(
                    title = title,
                    updatedAt = System.currentTimeMillis(),
                    messageCount = messages.size + 2
                )
            )

            val updatedMessages = chatDao.getMessages(conversation.id).first()
            ConversationMemoryService.updateMemory(conversation.id, updatedMessages)

            assistantMessage
        }
    }

    suspend fun generateImage(
        config: ModelConfig,
        conversationId: String,
        prompt: String,
        persona: Persona
    ): Result<ChatMessage> {
        val userTokenCount = TokenUtils.estimateTokenCount(prompt)
        val userMessage = ChatMessage(
            conversationId = conversationId,
            role = MessageRole.USER,
            content = prompt,
            contentType = MessageContentType.TEXT,
            tokenCount = userTokenCount
        )
        chatDao.insertMessage(userMessage)

        val result = ApiClient.generateImage(config, prompt)

        return result.map { imageUrl ->
            val assistantMessage = ChatMessage(
                conversationId = conversationId,
                role = MessageRole.ASSISTANT,
                content = imageUrl,
                contentType = MessageContentType.IMAGE,
                modelName = config.imageModelId
            )
            chatDao.insertMessage(assistantMessage)

            val firstMsg = chatDao.getFirstMessage(conversationId)
            val title = "[图片] $prompt".take(30)

            chatDao.updateConversation(
                Conversation(
                    id = conversationId,
                    title = title,
                    personaId = "",
                    modelConfigId = "",
                    updatedAt = System.currentTimeMillis()
                )
            )
            assistantMessage
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ChatRepository? = null

        fun getInstance(database: AppDatabase): ChatRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ChatRepository(database).also { INSTANCE = it }
            }
        }
    }
}
