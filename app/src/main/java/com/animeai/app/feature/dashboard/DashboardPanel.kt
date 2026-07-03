package com.animeai.app.feature.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.animeai.app.feature.emotion.EmotionAnalysis
import com.animeai.app.feature.emotion.EmotionChart
import com.animeai.app.util.TokenUtils

data class DashboardState(
    val totalTokens: Int = 0,
    val contextUsagePercent: Float = 0f,
    val messageCount: Int = 0,
    val activeModelName: String = "gpt-4o",
    val conversationDuration: String = "00:00",
    val contextInfo: ContextInfo = ContextInfo()
)

data class ContextInfo(
    val totalTokens: Int = 0,
    val usedPercentage: Float = 0f,
    val maxTokens: Int = 128000,
    val messagesCount: Int = 0
)

@Composable
fun DashboardPanel(
    isVisible: Boolean,
    dashboardState: DashboardState,
    emotionAnalysis: EmotionAnalysis?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut()
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("智能仪表盘", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "关闭", modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Token usage
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DashboardStat(
                        label = "Token 用量",
                        value = "${dashboardState.totalTokens}",
                        sub = "${dashboardState.contextUsagePercent.toInt()}%",
                        icon = Icons.Filled.DataUsage
                    )
                    DashboardStat(
                        label = "消息数",
                        value = "${dashboardState.messageCount}",
                        sub = "条消息",
                        icon = Icons.Filled.Chat
                    )
                    DashboardStat(
                        label = "模型",
                        value = dashboardState.activeModelName.take(10),
                        sub = if (dashboardState.activeModelName.length > 10) "\u2026" else "",
                        icon = Icons.Filled.Memory
                    )
                    DashboardStat(
                        label = "对话时长",
                        value = dashboardState.conversationDuration,
                        sub = "",
                        icon = Icons.Filled.Timer
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Context usage bar
                val contextInfo = dashboardState.contextInfo
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("上下文使用", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(TokenUtils.getContextUsageString(contextInfo.totalTokens), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { contextInfo.usedPercentage / 100f },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = when {
                            contextInfo.usedPercentage < 50 -> MaterialTheme.colorScheme.primary
                            contextInfo.usedPercentage < 80 -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.error
                        },
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                // Emotion chart
                if (emotionAnalysis != null && emotionAnalysis.points.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    EmotionChart(analysis = emotionAnalysis, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun DashboardStat(
    label: String,
    value: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (sub.isNotBlank()) {
            Text(text = sub, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        }
    }
}
