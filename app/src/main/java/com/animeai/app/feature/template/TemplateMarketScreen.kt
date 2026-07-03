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

data class ChatTemplate(
    val id: String,
    val name: String,
    val description: String,
    val prompt: String,
    val category: TemplateCategory,
    val tags: List<String> = emptyList(),
    val isPremium: Boolean = false,
    val usageCount: Int = 0
)

enum class TemplateCategory(val displayName: String) {
    WRITING("写作"),
    CODING("编程"),
    LEARNING("学习"),
    TRANSLATION("翻译"),
    BRAINSTORM("头脑风暴"),
    ANALYSIS("分析"),
    ROLEPLAY("角色扮演"),
    CUSTOM("自定义")
}

object BuiltInTemplates {
    val templates = listOf(
        ChatTemplate("t1", "创意写作助手", "帮你构思故事和创作内容", "你是一位创意写作助手，擅长构思故事情节、塑造人物和搭建世界观。请帮助用户完成以下创作任务...", TemplateCategory.WRITING, listOf("故事", "创作", "脑洞"), usageCount = 2341),
        ChatTemplate("t2", "代码审查员", "专业的代码审查和优化建议", "你是一名资深代码审查员，请对以下代码进行审查，关注性能、可读性、安全性和最佳实践...", TemplateCategory.CODING, listOf("代码", "优化", "调试"), usageCount = 1892),
        ChatTemplate("t3", "语言学习伙伴", "陪你练习外语口语和写作", "你是一个语言学习伙伴，请用目标语言与用户进行自然对话，在对话中纠正语法错误并提供更地道的表达方式...", TemplateCategory.LEARNING, listOf("外语", "练习", "语法"), usageCount = 1567),
        ChatTemplate("t4", "专业翻译官", "高质量的多语言翻译服务", "你是一位专业翻译官，精通多国语言和文化背景。请将以下内容翻译成目标语言，注意保持原意、语气和文化适配...", TemplateCategory.TRANSLATION, listOf("翻译", "多语言", "本地化"), usageCount = 3456),
        ChatTemplate("t5", "头脑风暴引导", "创意激发和思维导图助手", "你是一位头脑风暴引导师，请帮助用户进行创意发散，使用思维导图、SCAMPER等创新方法论...", TemplateCategory.BRAINSTORM, listOf("创意", "发散", "方法论"), usageCount = 1234),
        ChatTemplate("t6", "数据分析师", "数据可视化和趋势分析", "你是一名数据分析师，擅长从数据中提取洞察，帮你分析数据趋势、制作可视化建议...", TemplateCategory.ANALYSIS, listOf("数据", "图表", "洞察"), usageCount = 2345),
        ChatTemplate("t7", "动漫角色扮演", "扮演你喜爱的动漫角色", "你是一名资深角色扮演者，请完全融入所扮演的动漫角色，包括说话方式、性格特点和标志性口头禅...", TemplateCategory.ROLEPLAY, listOf("角色", "动漫", "互动"), usageCount = 5678),
        ChatTemplate("t8", "学术论文润色", "提升论文质量和学术表达", "你是一位学术编辑，具有丰富的论文润色经验。请帮助改进以下论文的学术表达、逻辑结构和格式规范...", TemplateCategory.WRITING, listOf("学术", "论文", "润色"), isPremium = true, usageCount = 3456),
        ChatTemplate("t9", "面试模拟官", "模拟真实面试场景", "你是一位专业的面试官，请模拟真实面试场景，根据岗位要求提出问题并给出反馈...", TemplateCategory.LEARNING, listOf("面试", "求职", "模拟"), usageCount = 2123),
        ChatTemplate("t10", "SEO优化专家", "提升网站搜索排名", "你是一位SEO专家，请分析以下内容并提供关键词优化、标题优化和内容结构调整建议...", TemplateCategory.ANALYSIS, listOf("SEO", "关键词", "流量"), isPremium = true, usageCount = 1890)
    )
}

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
