package com.animeai.app.feature.arena

import com.animeai.app.data.model.ChatMessage
import com.animeai.app.data.model.Persona

data class ArenaContender(
    val persona: Persona,
    val modelConfigId: String,
    var response: String? = null,
    var isLoading: Boolean = false,
    var isComplete: Boolean = false,
    var tokenCount: Int = 0,
    var responseTimeMs: Long = 0
)

data class ArenaSession(
    val id: String = java.util.UUID.randomUUID().toString(),
    val question: String,
    val contenders: List<ArenaContender>,
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val votes: Map<String, Int> = emptyMap()
)

data class ArenaVoteResult(
    val contenderIndex: Int,
    val percentage: Float
)
