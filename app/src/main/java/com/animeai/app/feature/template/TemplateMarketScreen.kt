package com.animeai.app.feature.template

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateMarketScreen(
    onBack: () -> Unit,
    onUseTemplate: (ChatTemplate) -> Unit,
    modifier: Modifier = Modifier
) {
    val templates = remember { BuiltInTemplates.templates }
    var selectedCategory by remember { mutableStateOf<TemplateCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTemplate by remember { mutableStateOf<ChatTemplate?>(null) }

    val filteredTemplates = templates.filter { t ->
        (selectedCategory == null || t.category == selectedCategory) &&
        (searchQuery.isBlank() || t.name.contains(searchQuery, ignoreCase = true) || t.description.contains(searchQuery, ignoreCase = true))
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
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回") }
                Text(text = "Prompt 魔导书", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "${templates.size} 个模板", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                placeholder = { Text("搜索模板…") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("全部", style = MaterialTheme.typography.labelSmall) }
                    )
                }
                items(TemplateCategory.entries) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = if (selectedCategory == category) null else category },
                        label = { Text(category.displayName, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredTemplates) { template ->
                    TemplateCard(
                        template = template,
                        onClick = { selectedTemplate = template },
                        onUse = { onUseTemplate(template) }
                    )
                }
            }
        }
    }

    selectedTemplate?.let { template ->
        AlertDialog(
            onDismissRequest = { selectedTemplate = null },
            title = { Text(template.name, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(template.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth()) {
                        Text(template.prompt, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(8.dp))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        template.tags.forEach { tag ->
                            SuggestionChip(onClick = {}, label = { Text(tag, style = MaterialTheme.typography.labelSmall) })
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onUseTemplate(template)
                    selectedTemplate = null
                }) { Text("使用此模板") }
            },
            dismissButton = { TextButton(onClick = { selectedTemplate = null }) { Text("关闭") } }
        )
    }
}

@Composable
private fun TemplateCard(
    template: ChatTemplate,
    onClick: () -> Unit,
    onUse: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = when (template.category) {
                            TemplateCategory.WRITING -> "\u270D"
                            TemplateCategory.CODING -> "\uD83D\uDCBB"
                            TemplateCategory.LEARNING -> "\uD83D\uDCDA"
                            TemplateCategory.TRANSLATION -> "\uD83C\uDF10"
                            TemplateCategory.BRAINSTORM -> "\uD83D\uDCA1"
                            TemplateCategory.ANALYSIS -> "\uD83D\uDCCA"
                            TemplateCategory.ROLEPLAY -> "\uD83C\uDFAD"
                            TemplateCategory.CUSTOM -> "\u2B50"
                        },
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = template.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(text = template.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                Text(text = template.category.displayName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onUse) {
                Icon(Icons.Filled.PlayArrow, contentDescription = "使用", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
