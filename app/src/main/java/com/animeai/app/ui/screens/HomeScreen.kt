package com.animeai.app.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.animeai.app.data.model.Conversation
import com.animeai.app.data.model.Persona
import com.animeai.app.service.PersonaService
import com.animeai.app.ui.components.PersonaAvatar
import com.animeai.app.ui.theme.*
import com.animeai.app.util.ModelIconMapper

data class FeatureItem(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val color: androidx.compose.ui.graphics.Color,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    conversations: List<Conversation>,
    personaMap: Map<String, Persona>,
    onNewChat: (Persona) -> Unit,
    onOpenChat: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onDeleteConversation: (String) -> Unit,
    onOpenArena: () -> Unit,
    onOpenCrossValidate: () -> Unit,
    onOpenTimeCapsule: () -> Unit,
    onOpenTree: () -> Unit,
    onOpenSandbox: () -> Unit,
    onOpenPipeline: () -> Unit,
    onOpenKnowledge: () -> Unit,
    onOpenTemplate: () -> Unit,
    onOpenPersonaTrainer: () -> Unit,
    onOpenShare: () -> Unit,
    onOpenSleep: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showPersonaSheet by remember { mutableStateOf(false) }
    var deleteConfirmId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showAllFeatures by remember { mutableStateOf(false) }

    val filteredConversations = if (searchQuery.isBlank()) conversations
    else conversations.filter { it.title.lowercase().contains(searchQuery.lowercase()) }

    val features = remember {
        listOf(
            FeatureItem("arena", "人格竞技场", "多AI同台PK", Icons.Filled.Compare, Pink300, onOpenArena),
            FeatureItem("cross", "交叉验证", "多模型对比", Icons.Filled.CompareArrows, Lavender500, onOpenCrossValidate),
            FeatureItem("capsule", "时间胶囊", "封存对话", Icons.Filled.AccessTime, Pink500, onOpenTimeCapsule),
            FeatureItem("tree", "对话树", "平行宇宙", Icons.Filled.AccountTree, Lavender300, onOpenTree),
            FeatureItem("sandbox", "代码沙箱", "运行代码", Icons.Filled.Code, MintGreen, onOpenSandbox),
            FeatureItem("pipeline", "AI工作流", "拖拽流水线", Icons.Filled.Hub, SkyBlue, onOpenPipeline),
            FeatureItem("knowledge", "知识库", "本地RAG", Icons.Filled.LibraryBooks, Pink200, onOpenKnowledge),
            FeatureItem("template", "魔导书", "Prompt模板", Icons.Filled.Book, Lavender200, onOpenTemplate),
            FeatureItem("trainer", "人格训练", "自定义人格", Icons.Filled.PersonAdd, Peach, onOpenPersonaTrainer),
            FeatureItem("share", "分享卡片", "精美截图", Icons.Filled.Share, Pink300, onOpenShare),
            FeatureItem("sleep", "睡眠模式", "AI梦境", Icons.Filled.NightsStay, Lavender500, onOpenSleep),
        )
    }

    val visibleFeatures = if (showAllFeatures) features else features.take(5)

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AnimeAI",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "你的二次元 AI 助手",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    onClick = onOpenSettings,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(modifier = Modifier.padding(10.dp)) {
                        Icon(Icons.Filled.Settings, contentDescription = "设置", modifier = Modifier.size(22.dp))
                    }
                }
            }

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                placeholder = { Text("搜索对话…") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Feature chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(visibleFeatures) { feature ->
                    FeatureChip(
                        feature = feature,
                        onClick = feature.onClick
                    )
                }
                if (!showAllFeatures && features.size > 5) {
                    item {
                        AssistChip(
                            onClick = { showAllFeatures = true },
                            label = { Text("+${features.size - 5}", style = MaterialTheme.typography.labelSmall) },
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }

            if (showAllFeatures && features.size > 5) {
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(
                    onClick = { showAllFeatures = false },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text("收起", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Section title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "最近对话",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (conversations.isNotEmpty()) {
                    Text(
                        text = "${conversations.size} 个对话",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Conversations
            if (filteredConversations.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        hasSearch = searchQuery.isNotBlank()
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .animateContentSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredConversations, key = { it.id }) { conversation ->
                        val persona = personaMap[conversation.personaId]
                        ConversationCard(
                            conversation = conversation,
                            persona = persona,
                            onClick = { onOpenChat(conversation.id) },
                            onDelete = { deleteConfirmId = conversation.id }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        // FAB
        ExtendedFloatingActionButton(
            onClick = { showPersonaSheet = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .navigationBarsPadding(),
            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
            text = { Text("新对话") }
        )
    }

    if (showPersonaSheet) {
        PersonaSelectionSheet(
            onDismiss = { showPersonaSheet = false },
            onSelect = { persona ->
                showPersonaSheet = false
                onNewChat(persona)
            }
        )
    }

    deleteConfirmId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteConfirmId = null },
            title = { Text("确认删除") },
            text = { Text("确定要删除这个对话吗？") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteConversation(id)
                    deleteConfirmId = null
                }) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmId = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun FeatureChip(feature: FeatureItem, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = feature.color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                feature.icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = feature.color
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = feature.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = feature.color
            )
        }
    }
}

@Composable
private fun EmptyState(hasSearch: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (hasSearch) Icons.Outlined.SearchOff else Icons.Outlined.ChatBubbleOutline,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (hasSearch) "没有找到匹配的对话" else "暂无对话",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline
        )
        if (!hasSearch) {
            Text(
                text = "点击下方按钮开启新对话",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ConversationCard(
    conversation: Conversation,
    persona: Persona?,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            persona?.let {
                PersonaAvatar(
                    persona = it,
                    size = 44.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = conversation.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (conversation.isPinned) {
                        Icon(
                            Icons.Filled.PushPin,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${conversation.messageCount} 条",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "·",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                    Text(
                        text = formatDate(conversation.updatedAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "删除",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonaSelectionSheet(
    onDismiss: () -> Unit,
    onSelect: (Persona) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "选择人格",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            val personas = PersonaService.getDefaultPersonas()
            val categories = personas.groupBy { it.type }

            categories.forEach { (type, personaList) ->
                val categoryName = when (type) {
                    com.animeai.app.data.model.PersonaType.ANIME -> "二次元人格"
                    com.animeai.app.data.model.PersonaType.ASSISTANT -> "助手人格"
                    com.animeai.app.data.model.PersonaType.DEVELOPER -> "开发工程师"
                }

                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp, top = 12.dp)
                )

                personaList.forEach { persona ->
                    PersonaSelectionItem(
                        persona = persona,
                        onClick = { onSelect(persona) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonaSelectionItem(
    persona: Persona,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PersonaAvatar(persona = persona, size = 44.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = persona.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = persona.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "刚刚"
        diff < 3600_000 -> "${diff / 60_000} 分钟前"
        diff < 86400_000 -> "${diff / 3600_000} 小时前"
        diff < 604800_000 -> "${diff / 86400_000} 天前"
        else -> {
            val sdf = java.text.SimpleDateFormat("MM/dd", java.util.Locale.getDefault())
            sdf.format(java.util.Date(timestamp))
        }
    }
}
