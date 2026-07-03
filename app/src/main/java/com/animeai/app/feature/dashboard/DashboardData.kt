package com.animeai.app.feature.dashboard

import com.animeai.app.util.TokenUtils

data class DashboardState(
    val totalTokens: Int = 0,
    val contextUsagePercent: Float = 0f,
    val messageCount: Int = 0,
    val avgResponseTokens: Int = 0,
    val activeModelName: String = "",
    val conversationDuration: String = "0m",
    val tokenHistory: List<TokenSnapshot> = emptyList(),
    val contextInfo: TokenUtils.ContextInfo = TokenUtils.ContextInfo(0, false, false, 0, 0f),
    val isVisible: Boolean = false
)

data class TokenSnapshot(
    val timestamp: Long,
    val tokens: Int,
    val label: String = ""
)

data class DashboardPreset(
    val name: String,
    val showTokenUsage: Boolean = true,
    val showEmotionChart: Boolean = true,
    val showModelInfo: Boolean = true,
    val showMemoryInfo: Boolean = true
)
