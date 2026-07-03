package com.animeai.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.animeai.app.data.model.ChatMessage
import com.animeai.app.data.model.MessageContentType
import com.animeai.app.data.model.MessageRole
import com.animeai.app.data.model.Persona
import com.animeai.app.util.ModelIconMapper
import com.animeai.app.util.TokenUtils

@Composable
fun MessageBubble(
    message: ChatMessage,
    persona: Persona?,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == MessageRole.USER
    val isImage = message.contentType == MessageContentType.IMAGE
    val iconInfo = ModelIconMapper.getModelIcon(message.modelName ?: "")

    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically { it / 2 }
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
            if (!isUser) {
                if (message.modelName != null) {
                    ModelAvatar(
                        modelName = message.modelName,
                        modifier = Modifier.padding(end = 8.dp, top = 4.dp)
                    )
                } else if (persona != null) {
                    PersonaAvatar(
                        persona = persona,
                        modifier = Modifier.padding(end = 8.dp, top = 4.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
                modifier = Modifier.weight(1f, fill = false).widthIn(max = 320.dp)
            ) {
                if (!isUser && message.modelName != null) {
                    ModelInfoBadge(
                        modelName = message.modelName,
                        modifier = Modifier.padding(bottom = 2.dp, start = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    ),
                    color = if (isUser)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        if (message.thinkingContent != null) {
                            ThinkingBlock(thinkingContent = message.thinkingContent)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (isImage) {
                            AsyncImage(
                                model = message.content,
                                contentDescription = null,
                                modifier = Modifier
                                    .widthIn(max = 250.dp)
                                    .heightIn(max = 250.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            MarkdownContent(content = message.content)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (message.tokenCount != null) {
                                Text(
                                    text = "${message.tokenCount} tokens",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = (if (isUser)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant).copy(alpha = 0.5f)
                                )
                            } else { Spacer(modifier = Modifier.width(1.dp)) }

                            Row {
                                Text(
                                    text = formatTime(message.timestamp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = (if (isUser)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant).copy(alpha = 0.7f)
                                )
                                message.modelName?.let {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = iconInfo.backgroundColor.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = iconInfo.displayName.ifEmpty { it },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = iconInfo.backgroundColor,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThinkingBlock(thinkingContent: String) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (expanded) "▼ 思考过程" else "▶ 思考过程",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${TokenUtils.estimateTokenCount(thinkingContent)} tokens",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = thinkingContent,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun PersonaAvatar(
    persona: Persona,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp
) {
    Surface(
        modifier = modifier.size(size),
        shape = RoundedCornerShape(size / 3),
        color = persona.avatarColor.copy(alpha = 0.3f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = persona.name.take(1),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "刚刚"
        diff < 3600_000 -> "${diff / 60_000}分钟前"
        else -> {
            val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            sdf.format(java.util.Date(timestamp))
        }
    }
}
