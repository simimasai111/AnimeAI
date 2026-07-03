package com.animeai.app.feature.arena

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.animeai.app.data.model.Persona
import com.animeai.app.service.PersonaService
import com.animeai.app.ui.components.ModelAvatar
import com.animeai.app.ui.components.PersonaAvatar
import com.animeai.app.ui.theme.Pink200
import com.animeai.app.ui.theme.Lavender200
import com.animeai.app.ui.theme.SkyBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ArenaContender(
    val persona: Persona,
    val modelConfigId: String,
    val response: String? = null,
    val isLoading: Boolean = true,
    val isComplete: Boolean = false,
    val tokenCount: Int = 0,
    val responseTimeMs: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArenaScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var question by remember { mutableStateOf("") }
    var contenders by remember { mutableStateOf<List<ArenaContender>>(emptyList()) }
    var isBattleStarted by remember { mutableStateOf(false) }
    var votes by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var hasVoted by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val personas = remember {
        PersonaService.getDefaultPersonas().take(3)
    }

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
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI 人格竞技场",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = question,
                        onValueChange = { question = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("输入挑战问题…") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
                        ),
                        maxLines = 2,
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilledIconButton(
                        onClick = {
                            if (question.isNotBlank()) {
                                contenders = personas.map { p ->
                                    ArenaContender(
                                        persona = p,
                                        modelConfigId = "openai"
                                    )
                                }
                                isBattleStarted = true
                                hasVoted = false
                                votes = emptyMap()
                                scope.launch {
                                    contenders.forEachIndexed { index, _ ->
                                        delay(500 + index * 1500L)
                                        val mockResponse = listOf(
                                            "关于「$question」，我的看法是：这是一个很有趣的问题！从多个角度来分析…\n\n首先，我们可以从技术层面考虑...\n\n其次，从用户体验角度来看...\n\n总的来说，这是一个值得深入探讨的话题。",
                                            "让我来回答「$question」这个问题。\n\n## 核心观点\n\n这个问题的关键在于...\n\n## 详细分析\n\n1. 第一点...\n2. 第二点...\n3. 第三点...\n\n## 结论\n\n综上所述，我认为...",
                                            "关于「$question」喵～\n\n让我想想哦～\n\n首先呢，这个问题其实涉及到好几个方面：\n- 技术层面\n- 用户体验\n- 未来发展\n\n我觉得最重要的是要理解用户真正需要的是什么喵！\n\n以上就是我的想法～"
                                        )
                                        val updated = contenders.toMutableList()
                                        updated[index] = updated[index].copy(
                                            response = mockResponse[index],
                                            isLoading = false,
                                            isComplete = true,
                                            tokenCount = mockResponse[index].length / 4,
                                            responseTimeMs = (300 + index * 200).toLong()
                                        )
                                        contenders = updated
                                    }
                                }
                            }
                        },
                        enabled = question.isNotBlank() && !isBattleStarted,
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "开战", modifier = Modifier.size(24.dp))
                    }
                }
            }

            if (isBattleStarted) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    contenders.forEachIndexed { index, contender ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            PersonaAvatar(
                                persona = contender.persona,
                                size = 40.dp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = contender.persona.name,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                color = when (index) {
                                    0 -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                    1 -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                                    2 -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ) {
                                Box(modifier = Modifier.padding(8.dp)) {
                                    if (contender.isLoading) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "思考中…",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    } else if (contender.isComplete && contender.response != null) {
                                        LazyColumn {
                                            item {
                                                Text(
                                                    text = contender.response,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = "${contender.tokenCount} tokens",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Text(
                                                        text = "${contender.responseTimeMs}ms",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }

                                                if (!hasVoted && contenders.all { it.isComplete }) {
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Button(
                                                        onClick = {
                                                            votes = mapOf(index to ((votes[index] ?: 0) + 1))
                                                            hasVoted = true
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = MaterialTheme.colorScheme.primary
                                                        ),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Text("投给 ${contender.persona.name}", style = MaterialTheme.typography.labelSmall)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (hasVoted) {
                                Spacer(modifier = Modifier.height(4.dp))
                                val totalVotes = votes.values.sum().coerceAtLeast(1)
                                val voteCount = votes[index] ?: 0
                                val percentage = (voteCount.toFloat() / totalVotes * 100).toInt()
                                LinearProgressIndicator(
                                    progress = { voteCount.toFloat() / totalVotes },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = when (index) {
                                        0 -> MaterialTheme.colorScheme.primary
                                        1 -> MaterialTheme.colorScheme.secondary
                                        2 -> MaterialTheme.colorScheme.tertiary
                                        else -> MaterialTheme.colorScheme.primary
                                    }
                                )
                                Text(
                                    text = "$percentage% ($voteCount 票)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.Compare,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "让多个AI人格同台竞技",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "输入问题，看看谁的回答更出色",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            personas.forEach { p ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    PersonaAvatar(persona = p, size = 48.dp)
                                    Text(text = p.name, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
