package com.animeai.app.feature.persona_trainer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.animeai.app.data.model.PersonaType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonaTrainerScreen(
    onBack: () -> Unit,
    onSave: (PersonaBlueprint) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var background by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(PersonaType.ANIME) }
    var formality by remember { mutableFloatStateOf(0.5f) }
    var warmth by remember { mutableFloatStateOf(0.7f) }
    var humor by remember { mutableFloatStateOf(0.5f) }
    var professionalism by remember { mutableFloatStateOf(0.3f) }
    var creativity by remember { mutableFloatStateOf(0.7f) }
    var talkativeness by remember { mutableFloatStateOf(0.5f) }
    var useEmoji by remember { mutableStateOf(true) }
    var useSlang by remember { mutableStateOf(false) }
    var showPreview by remember { mutableStateOf(false) }

    val generatedPrompt = remember(name, description, formality, warmth, humor, professionalism, creativity, talkativeness) {
        PersonaPromptGenerator.generate(
            PersonaBlueprint(
                name = name.ifBlank { "未命名人格" },
                description = description,
                type = selectedType,
                personalityTraits = PersonalityTraits(formality, warmth, humor, professionalism, creativity, talkativeness),
                speakingStyle = SpeakingStyle(useEmoji = useEmoji, useSlang = useSlang),
                background = background
            )
        )
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
                Text(text = "人格训练场", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("人格名称") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("一句话描述") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = background, onValueChange = { background = it }, label = { Text("背景设定") }, maxLines = 4, modifier = Modifier.fillMaxWidth())

                Text(text = "人格类型", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PersonaType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(
                                when (type) {
                                    PersonaType.ANIME -> "二次元"
                                    PersonaType.ASSISTANT -> "助手"
                                    PersonaType.DEVELOPER -> "开发者"
                                }
                            )}
                        )
                    }
                }

                Text(text = "性格参数", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                TraitSlider(label = "正式程度", value = formality, onValueChange = { formality = it })
                TraitSlider(label = "温暖程度", value = warmth, onValueChange = { warmth = it })
                TraitSlider(label = "幽默感", value = humor, onValueChange = { humor = it })
                TraitSlider(label = "专业度", value = professionalism, onValueChange = { professionalism = it })
                TraitSlider(label = "创造力", value = creativity, onValueChange = { creativity = it })
                TraitSlider(label = "话痨程度", value = talkativeness, onValueChange = { talkativeness = it })

                Text(text = "说话风格", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = useEmoji, onClick = { useEmoji = !useEmoji }, label = { Text("使用表情") }, leadingIcon = { if (useEmoji) Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp)) })
                    FilterChip(selected = useSlang, onClick = { useSlang = !useSlang }, label = { Text("使用网络语") }, leadingIcon = { if (useSlang) Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp)) })
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showPreview = !showPreview },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (showPreview) "隐藏预览" else "预览生成的人格")
                }

                if (showPreview) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = generatedPrompt,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        onSave(
                            PersonaBlueprint(
                                name = name.ifBlank { "自定义人格" },
                                description = description,
                                type = selectedType,
                                personalityTraits = PersonalityTraits(formality, warmth, humor, professionalism, creativity, talkativeness),
                                speakingStyle = SpeakingStyle(useEmoji = useEmoji, useSlang = useSlang),
                                background = background,
                                generatedSystemPrompt = generatedPrompt,
                                greeting = "你好！我是${name.ifBlank { "自定义人格" }}，很高兴认识你！"
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = name.isNotBlank()
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("保存人格")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TraitSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(text = "${(value * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
