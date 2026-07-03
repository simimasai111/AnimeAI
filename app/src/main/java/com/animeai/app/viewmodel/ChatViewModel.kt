package com.animeai.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.animeai.app.data.local.AppDatabase
import com.animeai.app.data.model.*
import com.animeai.app.data.repository.ChatRepository
import com.animeai.app.data.repository.ConfigRepository
import com.animeai.app.feature.dashboard.DashboardState
import com.animeai.app.feature.emotion.EmotionAnalysis
import com.animeai.app.feature.emotion.EmotionAnalyzer
import com.animeai.app.feature.persona_trainer.PersonaBlueprint
import com.animeai.app.feature.template.ChatTemplate
import com.animeai.app.service.ConversationMemoryService
import com.animeai.app.service.PersonaService
import com.animeai.app.util.TokenUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val chatRepository = ChatRepository.getInstance(database)
    private val configRepository = ConfigRepository.getInstance(application)

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _currentConversation = MutableStateFlow<Conversation?>(null)
    val currentConversation: StateFlow<Conversation?> = _currentConversation.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _currentPersona = MutableStateFlow<Persona?>(null)
    val currentPersona: StateFlow<Persona?> = _currentPersona.asStateFlow()

    private val _activeConfig = MutableStateFlow<ModelConfig?>(null)
    val activeConfig: StateFlow<ModelConfig?> = _activeConfig.asStateFlow()

    private val _dashboardState = MutableStateFlow(DashboardState())
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()

    private val _emotionAnalysis = MutableStateFlow<EmotionAnalysis?>(null)
    val emotionAnalysis: StateFlow<EmotionAnalysis?> = _emotionAnalysis.asStateFlow()

    private val _customPersonas = MutableStateFlow<List<PersonaBlueprint>>(emptyList())
    val customPersonas: StateFlow<List<PersonaBlueprint>> = _customPersonas.asStateFlow()

    init {
        viewModelScope.launch {
            chatRepository.getAllConversations().collect { list ->
                _conversations.value = ConversationMemoryService.sortConversations(list, emptyMap())
            }
        }
        refreshConfig()
    }

    fun refreshConfig() {
        _activeConfig.value = configRepository.getActiveConfig()
    }

    fun getAllConfigs(): List<ModelConfig> = configRepository.getAllConfigs()

    fun selectConfig(id: String) {
        configRepository.setActiveConfig(id)
        refreshConfig()
        _error.value = null
    }

    fun saveCustomConfig(config: ModelConfig) {
        configRepository.saveCustomConfig(config)
        refreshConfig()
    }

    fun deleteCustomConfig(id: String) {
        configRepository.deleteCustomConfig(id)
        refreshConfig()
    }

    fun createNewConversation(persona: Persona) {
        viewModelScope.launch {
            val config = _activeConfig.value ?: return@launch
            try {
                val conversation = chatRepository.createConversation(
                    persona = persona,
                    modelConfigId = config.id
                )
                _currentConversation.value = conversation
                _currentPersona.value = persona
                _messages.value = emptyList()
                _emotionAnalysis.value = null
                resetDashboard()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "创建对话失败: ${e.message}"
            }
        }
    }

    fun loadConversation(conversationId: String) {
        viewModelScope.launch {
            chatRepository.getConversation(conversationId).collect { conversation ->
                _currentConversation.value = conversation
                if (conversation != null) {
                    _currentPersona.value = PersonaService.getPersonaById(conversation.personaId)
                    val config = configRepository.getConfigById(conversation.modelConfigId)
                    if (config != null) {
                        _activeConfig.value = config
                    }
                }
            }
        }
        viewModelScope.launch {
            chatRepository.getMessages(conversationId).collect { msgList ->
                _messages.value = msgList
                updateDashboard(msgList)
            }
        }
    }

    fun sendMessage(text: String) {
        val conversation = _currentConversation.value ?: return
        val config = _activeConfig.value ?: return
        val persona = _currentPersona.value ?: return

        val userMessage = ChatMessage(
            conversationId = conversation.id,
            role = MessageRole.USER,
            content = text
        )

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = chatRepository.sendMessage(config, conversation, userMessage, persona)
            result.onFailure { e ->
                _error.value = "发送失败: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    fun generateImage(prompt: String) {
        val conversation = _currentConversation.value ?: return
        val config = _activeConfig.value ?: return
        val persona = _currentPersona.value ?: return

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = chatRepository.generateImage(config, conversation.id, prompt, persona)
            result.onFailure { e ->
                _error.value = "图片生成失败: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            chatRepository.deleteConversation(id)
            if (_currentConversation.value?.id == id) {
                _currentConversation.value = null
                _currentPersona.value = null
                _messages.value = emptyList()
                resetDashboard()
            }
        }
    }

    fun toggleDashboard() {
        _dashboardState.update { it.copy(isVisible = !it.isVisible) }
    }

    fun useTemplate(template: ChatTemplate) {
        val persona = _currentPersona.value ?: return
        val text = template.prompt
        sendMessage(text)
    }

    fun saveCustomPersona(blueprint: PersonaBlueprint) {
        _customPersonas.update { it + blueprint }
        _error.value = null
    }

    fun clearError() {
        _error.value = null
    }

    fun goBack() {
        _currentConversation.value = null
        _currentPersona.value = null
        _messages.value = emptyList()
        resetDashboard()
        _error.value = null
    }

    private fun resetDashboard() {
        _dashboardState.value = DashboardState()
        _emotionAnalysis.value = null
    }

    private fun updateDashboard(messages: List<ChatMessage>) {
        if (messages.isEmpty()) return

        val contentList = messages.map { it.content }
        val contextInfo = TokenUtils.analyzeContext(contentList)
        val totalTokens = contentList.sumOf { TokenUtils.estimateTokenCount(it) }
        val config = _activeConfig.value

        // Emotion analysis
        val analysis = EmotionAnalyzer.analyzeConversation(messages)
        _emotionAnalysis.value = analysis

        // Update memory
        ConversationMemoryService.updateMemory(
            messages.first().conversationId,
            messages
        )

        val firstMsgTime = messages.firstOrNull()?.timestamp ?: System.currentTimeMillis()
        val duration = System.currentTimeMillis() - firstMsgTime
        val durationStr = when {
            duration < 3600_000 -> "${duration / 60_000}分钟"
            duration < 86400_000 -> "${duration / 3600_000}小时"
            else -> "${duration / 86400_000}天"
        }

        _dashboardState.value = DashboardState(
            totalTokensUsed = totalTokens,
            contextUsagePercent = contextInfo.usedPercentage,
            messageCount = messages.size,
            avgResponseTokens = totalTokens / messages.size.coerceAtLeast(1),
            activeModelName = config?.modelId ?: "",
            conversationDuration = durationStr,
            contextInfo = contextInfo,
            isVisible = _dashboardState.value.isVisible
        )
    }
}
