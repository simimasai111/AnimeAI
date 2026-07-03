package com.animeai.app.feature.workflow

data class Pipeline(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val nodes: List<PipelineNode>,
    val edges: List<PipelineEdge>,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = false
)

data class PipelineNode(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: NodeType,
    val label: String,
    val config: Map<String, String> = emptyMap(),
    val positionX: Float = 0f,
    val positionY: Float = 0f,
    val inputPorts: List<String> = listOf("input"),
    val outputPorts: List<String> = listOf("output")
)

enum class NodeType(val displayName: String) {
    INPUT("输入"),
    PROMPT("提示词"),
    LLM_CALL("LLM 调用"),
    IMAGE_GEN("图片生成"),
    CODE_EXEC("代码执行"),
    CONDITION("条件判断"),
    MERGE("合并"),
    OUTPUT("输出"),
    FILTER("过滤"),
    TRANSFORM("转换")
}

data class PipelineEdge(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sourceNodeId: String,
    val targetNodeId: String,
    val sourcePort: String = "output",
    val targetPort: String = "input"
)

data class PipelineExecution(
    val pipelineId: String,
    val nodeResults: Map<String, String> = emptyMap(),
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val isRunning: Boolean = false,
    val error: String? = null
)
