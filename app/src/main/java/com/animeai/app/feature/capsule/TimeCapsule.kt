package com.animeai.app.feature.capsule

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_capsules")
data class TimeCapsule(
    @PrimaryKey
    val id: String,
    val conversationId: String,
    val title: String,
    val summary: String,
    val createdAt: Long = System.currentTimeMillis(),
    val unlockAt: Long,
    val isOpened: Boolean = false,
    val openedAt: Long? = null,
    val messageCount: Int = 0,
    val totalTokens: Int = 0,
    val visualStyle: String = "default"
)
