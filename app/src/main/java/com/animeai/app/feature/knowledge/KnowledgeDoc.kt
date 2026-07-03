package com.animeai.app.feature.knowledge

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "knowledge_docs")
data class KnowledgeDoc(
    @PrimaryKey
    val id: String,
    val title: String,
    val fileName: String,
    val content: String,
    val summary: String = "",
    val fileType: String = "txt",
    val fileSize: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val chunkCount: Int = 0,
    val isIndexed: Boolean = false
)

data class KnowledgeChunk(
    val docId: String,
    val index: Int,
    val content: String,
    val embedding: List<Float>? = null
)

data class KnowledgeSearchResult(
    val docId: String,
    val docTitle: String,
    val content: String,
    val relevance: Float,
    val chunkIndex: Int
)
