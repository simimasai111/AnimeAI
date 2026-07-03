package com.animeai.app.feature.sleep

data class SleepSession(
    val id: String = java.util.UUID.randomUUID().toString(),
    val conversationId: String,
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long? = null,
    val thoughtsGenerated: Int = 0,
    val lastThought: String = "",
    val dreamSummary: String = ""
)

data class SleepThought(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sessionId: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val topic: String = "",
    val isSignificant: Boolean = false
)
