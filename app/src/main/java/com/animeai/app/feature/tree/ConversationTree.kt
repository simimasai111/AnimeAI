package com.animeai.app.feature.tree

data class TreeNode(
    val id: String,
    val conversationId: String,
    val parentId: String?,
    val messageId: Long,
    val content: String,
    val branchName: String = "",
    val children: List<String> = emptyList(),
    val depth: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class TreeBranch(
    val nodeId: String,
    val nodes: List<TreeNode>,
    val conversationIds: List<String>
)

data class TreeState(
    val currentBranchId: String? = null,
    val branches: Map<String, TreeBranch> = emptyMap(),
    val activeConversationId: String? = null
)
