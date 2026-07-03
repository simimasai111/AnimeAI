package com.animeai.app.feature.tree

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.animeai.app.ui.theme.Pink200
import com.animeai.app.ui.theme.Lavender200
import com.animeai.app.ui.theme.SkyBlue
import com.animeai.app.ui.theme.MintGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreeScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var branches by remember { mutableStateOf<List<TreeBranch>>(emptyList()) }

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
                    text = "对话树 · 平行宇宙",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (branches.isEmpty()) {
                EmptyTreeView()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(branches) { branch ->
                        BranchCard(branch = branch)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyTreeView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(modifier = Modifier.size(120.dp)) {
                val cx = size.width / 2
                val cy = size.height / 2
                val path = Path().apply {
                    moveTo(cx, cy - 40)
                    lineTo(cx - 30, cy + 20)
                    lineTo(cx + 10, cy + 10)
                    lineTo(cx + 30, cy + 30)
                }
                drawPath(path, Color(0xFF9850FF).copy(alpha = 0.4f),
                    style = Stroke(3f, cap = StrokeCap.Round))
                drawCircle(Color(0xFF9850FF).copy(alpha = 0.6f), 8f, Offset(cx, cy - 40))
                drawCircle(Color(0xFF9850FF).copy(alpha = 0.6f), 6f, Offset(cx - 30, cy + 20))
                drawCircle(Color(0xFF9850FF).copy(alpha = 0.6f), 6f, Offset(cx + 10, cy + 10))
                drawCircle(Color(0xFF9850FF).copy(alpha = 0.6f), 6f, Offset(cx + 30, cy + 30))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "还没有对话分支",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = "在对话中点击「分支」按钮创建平行宇宙",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
private fun BranchCard(branch: TreeBranch) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Lavender200.copy(alpha = 0.5f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.AccountTree,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "分支 ${branch.conversationIds.size} 个对话",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${branch.nodes.size} 个节点",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(60.dp)
            ) {
                val nodes = branch.nodes
                if (nodes.isEmpty()) return@Canvas
                val startX = 40f
                val endX = size.width - 40f
                val stepX = (endX - startX) / (nodes.size - 1).coerceAtLeast(1)
                val midY = size.height / 2

                nodes.forEachIndexed { i, node ->
                    val x = startX + i * stepX
                    val isBranchPoint = node.children.size > 1
                    drawCircle(
                        color = if (isBranchPoint) Color(0xFFFF6B81) else Color(0xFF9850FF),
                        radius = if (isBranchPoint) 8f else 5f,
                        center = Offset(x, midY)
                    )
                    if (i > 0) {
                        val prevX = startX + (i - 1) * stepX
                        drawLine(
                            color = Color(0xFF9850FF).copy(alpha = 0.3f),
                            start = Offset(prevX, midY),
                            end = Offset(x, midY),
                            strokeWidth = 2f
                        )
                    }
                }
            }
        }
    }
}
