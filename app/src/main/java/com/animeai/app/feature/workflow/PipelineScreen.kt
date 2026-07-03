package com.animeai.app.feature.workflow

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.animeai.app.ui.theme.Pink200
import com.animeai.app.ui.theme.Lavender200
import com.animeai.app.ui.theme.SkyBlue
import com.animeai.app.ui.theme.MintGreen
import com.animeai.app.ui.theme.Peach

data class PipelineNode(
    val id: String = java.util.UUID.randomUUID().toString().take(8),
    val type: NodeType,
    val label: String,
    val positionX: Float = 0f,
    val positionY: Float = 0f
)

enum class NodeType(val displayName: String) {
    INPUT("输入"),
    PROMPT("提示词"),
    LLM_CALL("LLM 调用"),
    IMAGE_GEN("图片生成"),
    CODE_EXEC("代码执行"),
    CONDITION("条件分支"),
    MERGE("合并"),
    FILTER("过滤"),
    TRANSFORM("转换"),
    OUTPUT("输出")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PipelineScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nodes by remember {
        mutableStateOf(
            listOf(
                PipelineNode(type = NodeType.INPUT, label = "输入", positionX = 50f, positionY = 200f),
                PipelineNode(type = NodeType.PROMPT, label = "翻译提示词", positionX = 250f, positionY = 100f),
                PipelineNode(type = NodeType.LLM_CALL, label = "调用 LLM", positionX = 250f, positionY = 300f),
                PipelineNode(type = NodeType.OUTPUT, label = "输出结果", positionX = 450f, positionY = 200f)
            )
        )
    }
    var selectedNodeId by remember { mutableStateOf<String?>(null) }
    var showAddMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                }
                Text(
                    text = "AI 工作流",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${nodes.size} 个节点",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AssistChip(
                        onClick = { showAddMenu = true },
                        label = { Text("添加节点") },
                        leadingIcon = { Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    AssistChip(
                        onClick = { /* Run pipeline */ },
                        label = { Text("运行") },
                        leadingIcon = { Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    AssistChip(
                        onClick = { nodes = emptyList() },
                        label = { Text("清空") },
                        leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    for (i in 0 until nodes.size - 1) {
                        val from = nodes[i]
                        val to = nodes[i + 1]
                        val startX = from.positionX + 60f
                        val startY = from.positionY + 20f
                        val endX = to.positionX
                        val endY = to.positionY + 20f

                        val path = Path().apply {
                            moveTo(startX, startY)
                            cubicTo(
                                (startX + endX) / 2, startY,
                                (startX + endX) / 2, endY,
                                endX, endY
                            )
                        }
                        drawPath(
                            path,
                            Color(0xFF9850FF).copy(alpha = 0.3f),
                            style = Stroke(2f, cap = StrokeCap.Round)
                        )
                        drawCircle(Color(0xFF9850FF).copy(alpha = 0.5f), 4f, Offset(endX, endY))
                    }
                }

                nodes.forEach { node ->
                    NodeView(
                        node = node,
                        isSelected = node.id == selectedNodeId,
                        onClick = { selectedNodeId = node.id },
                        modifier = Modifier.offset(
                            x = (node.positionX * 1.5).dp,
                            y = (node.positionY * 1.5).dp
                        )
                    )
                }
            }
        }
    }

    if (showAddMenu) {
        AlertDialog(
            onDismissRequest = { showAddMenu = false },
            title = { Text("添加节点") },
            text = {
                Column {
                    NodeType.entries.forEach { type ->
                        TextButton(
                            onClick = {
                                nodes = nodes + PipelineNode(
                                    type = type,
                                    label = type.displayName,
                                    positionX = (nodes.size * 180f) % 600f,
                                    positionY = ((nodes.size * 180f) / 600f).toInt() * 150f + 50f
                                )
                                showAddMenu = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(type.displayName)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddMenu = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun NodeView(
    node: PipelineNode,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nodeColor = when (node.type) {
        NodeType.INPUT -> MintGreen
        NodeType.PROMPT -> SkyBlue
        NodeType.LLM_CALL -> Lavender200
        NodeType.IMAGE_GEN -> Pink200
        NodeType.CODE_EXEC -> Peach
        NodeType.CONDITION -> Color(0xFFFFD700)
        NodeType.MERGE -> Color(0xFFE8D0F0)
        NodeType.OUTPUT -> Color(0xFFD4F5E2)
        NodeType.FILTER -> Color(0xFFFFDAB9)
        NodeType.TRANSFORM -> Color(0xFFD0E8FF)
    }

    Surface(
        onClick = onClick,
        modifier = modifier.width(140.dp),
        shape = RoundedCornerShape(12.dp),
        color = nodeColor.copy(alpha = 0.3f),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null,
        shadowElevation = if (isSelected) 4.dp else 1.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (node.type) {
                    NodeType.INPUT -> "\uD83D\uDCE5"
                    NodeType.PROMPT -> "\uD83D\uDCDD"
                    NodeType.LLM_CALL -> "\uD83E\uDD16"
                    NodeType.IMAGE_GEN -> "\uD83C\uDFA8"
                    NodeType.CODE_EXEC -> "\uD83D\uDCBB"
                    NodeType.CONDITION -> "\uD83D\uDD00"
                    NodeType.MERGE -> "\uD83D\uDD17"
                    NodeType.OUTPUT -> "\uD83D\uDCE4"
                    NodeType.FILTER -> "\uD83D\uDD0D"
                    NodeType.TRANSFORM -> "\uD83D\uDD04"
                },
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = node.label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = node.type.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
