package com.animeai.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.animeai.app.data.model.ChatMessage
import com.animeai.app.data.model.MessageRole
import com.animeai.app.data.model.Persona
import com.animeai.app.feature.dashboard.DashboardPanel
import com.animeai.app.feature.dashboard.DashboardState
import com.animeai.app.feature.emotion.EmotionAnalysis
import com.animeai.app.feature.emotion.EmotionChart
import com.animeai.app.service.ConversationMemoryService
import com.animeai.app.ui.components.MessageBubble
import com.animeai.app.ui.components.PersonaAvatar
import com.animeai.app.ui.components.ThinkingIndicator
import com.animeai.app.ui.components.glass.FloatingActionChips
import com.animeai.app.ui.components.glass.FloatingInputBar
import com.animeai.app.util.ModelIconMapper
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    persona: Persona?,
    isLoading: Boolean,
    dashboardState: DashboardState,
    emotionAnalysis: EmotionAnalysis?,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onGenerateImage: (String) -> Unit,
    onToggleDashboard: () -> Unit,
    onOpenArena: () -> Unit,
    onOpenCrossValidate: () -> Unit,
    onOpenBranch: () -> Unit,
    onOpenShare: () -> Unit,
    onOpenTemplate: () -> Unit,
    title: String,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var showActionChips by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Full screen message list
        Column(modifier = Modifier.fillMaxSize()) {
            // Minimal header overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                }

                Spacer(modifier = Modifier.width(4.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    onClick = onToggleDashboard,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (persona != null) {
                            PersonaAvatar(persona = persona, size = 28.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = persona?.name ?: title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                            if (dashboardState.totalTokens > 0) {
                                Text(
                                    text = "${dashboardState.contextUsagePercent.toInt()}% · ${messages.size}条",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Dashboard toggle
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    onClick = showActionChips.let({ { showActionChips = !showActionChips } })
                ) {
                    Box(modifier = Modifier.padding(8.dp)) {
                        Icon(
                            if (showActionChips) Icons.Filled.Close else Icons.Filled.MoreHoriz,
                            contentDescription = "更多",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Dashboard panel (collapsible)
            DashboardPanel(
                isVisible = dashboardState.isVisible,
                dashboardState = dashboardState,
                emotionAnalysis = emotionAnalysis,
                onDismiss = onToggleDashboard
            )

            // Emotion mini chart
            AnimatedVisibility(visible = emotionAnalysis != null && emotionAnalysis!!.points.size > 1 && !dashboardState.isVisible) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.ShowChart, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.width(4.dp))
                        emotionAnalysis?.let {
                            Text(
                                text = it.dominantEmotions.joinToString(" · ") { em ->
                                    com.animeai.app.feature.emotion.emotionColorMap.find { e -> e.emotion == em }?.emoji ?: em
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // Messages
            Box(modifier = Modifier.weight(1f)) {
                if (messages.isEmpty() && persona != null) {
                    GreetingMessage(persona = persona)
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 8.dp)
                ) {
                    items(messages, key = { it.id }) { message ->
                        MessageBubble(
                            message = message,
                            persona = if (message.role == MessageRole.USER) null else persona
                        )
                    }

                    if (isLoading) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                persona?.let {
                                    PersonaAvatar(
                                        persona = it,
                                        modifier = Modifier.padding(end = 8.dp, top = 4.dp)
                                    )
                                }
                                ThinkingIndicator()
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        // Floating action chips (overlay)
        FloatingActionChips(
            visible = showActionChips,
            onDismiss = { showActionChips = false },
            onArena = { showActionChips = false; onOpenArena() },
            onCrossValidate = { showActionChips = false; onOpenCrossValidate() },
            onBranch = { showActionChips = false; onOpenBranch() },
            onShare = { showActionChips = false; onOpenShare() },
            onTemplate = { showActionChips = false; onOpenTemplate() },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 72.dp)
        )

        // Floating input bar
        FloatingInputBar(
            value = inputText,
            onValueChange = { inputText = it },
            onSend = {
                val text = inputText.trim()
                if (text.isNotEmpty() && !isLoading) {
                    if (text.startsWith("/image ")) {
                        onGenerateImage(text.removePrefix("/image "))
                    } else {
                        onSendMessage(text)
                    }
                    inputText = ""
                }
            },
            onImageRequest = { inputText = "/image " },
            onAttach = { showActionChips = !showActionChips },
            enabled = !isLoading,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun GreetingMessage(persona: Persona) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            PersonaAvatar(
                persona = persona,
                modifier = Modifier,
                size = 80.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = persona.greeting,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
