package com.animeai.app.feature.sleep

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class SleepThought(
    val id: String,
    val sessionId: String,
    val content: String,
    val topic: String,
    val isSignificant: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepModeScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var thoughts by remember { mutableStateOf(
        listOf(
            SleepThought(id = "1", sessionId = "s1", content = "用户今天问了很多关于 AI 技术的问题，他好像对这个领域特别感兴趣。下次我可以准备一些更深入的技术细节来回答。", topic = "用户分析", isSignificant = true),
            SleepThought(id = "2", sessionId = "s1", content = "关于那个代码优化的问题，我其实还可以给出更高效的解法，下次要记得补充。", topic = "代码优化"),
            SleepThought(id = "3", sessionId = "s1", content = "用户提到最近在看一本关于机器学习的书，我应该整理一下相关的学习资源推荐给他。", topic = "学习建议", isSignificant = true),
            SleepThought(id = "4", sessionId = "s1", content = "今天的对话中有几个有趣的话题可以继续深入探讨…", topic = "话题延续")
        )
    ) }
    var isSleeping by remember { mutableStateOf(true) }

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
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回") }
                Text(text = "AI 睡眠模式", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            // Sleep status
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                color = if (isSleeping) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = if (isSleeping) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                if (isSleeping) Icons.Filled.NightsStay else Icons.Filled.WbSunny,
                                contentDescription = null,
                                tint = if (isSleeping) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isSleeping) "AI 正在梦中思考…" else "AI 已苏醒",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isSleeping) "${thoughts.size} 条梦境思绪" else "本次睡眠产生 ${thoughts.size} 条想法",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isSleeping,
                        onCheckedChange = { isSleeping = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (thoughts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.NightsStay, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "AI 还没有做过梦", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.outline)
                        Text(text = "多聊聊天，AI 会在你离开后继续思考", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f), textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(thoughts) { thought ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = if (thought.isSignificant) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                else MaterialTheme.colorScheme.surface
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                                    ) {
                                        Text(thought.topic, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    if (thought.isSignificant) {
                                        Icon(Icons.Filled.AutoAwesome, contentDescription = "重要", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = thought.content, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }
    }
}
