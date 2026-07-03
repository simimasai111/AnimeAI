package com.animeai.app.feature.share

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.animeai.app.ui.theme.Pink200
import com.animeai.app.ui.theme.Lavender200
import com.animeai.app.ui.theme.SkyBlue
import com.animeai.app.ui.theme.MintGreen

data class CardStyle(val name: String, val primaryColor: Color, val secondaryColor: Color, val emoji: String)

private val cardStyles = listOf(
    CardStyle("粉色甜心", Color(0xFFFFF0F5), Color(0xFFFFB3D0), "\uD83C\uDF38"),
    CardStyle("极简黑白", Color(0xFF1C1C1E), Color(0xFFF5F5F5), "\u25FC"),
    CardStyle("代码风格", Color(0xFF1E1E1E), Color(0xFF00FF88), "\uD83D\uDCBB"),
    CardStyle("淡紫幻境", Color(0xFFF3E8FF), Color(0xFF9850FF), "\uD83D\uDD2E"),
    CardStyle("天空之境", Color(0xFFE3F2FD), Color(0xFF42A5F5), "\uD83C\uDF0A"),
    CardStyle("暖阳橘光", Color(0xFFFFF3E0), Color(0xFFFF9800), "\u2600")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareCardScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStyle by remember { mutableStateOf(cardStyles[0]) }
    var content by remember { mutableStateOf("用户：今天天气真好\n\nAI：是啊！阳光明媚，适合出去走走～\n\n用户：你有什么推荐的地方吗？\n\nAI：我建议可以去公园散步，或者去海边吹吹风！") }
    var showShareOptions by remember { mutableStateOf(false) }

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
                Text(text = "分享卡片", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            // Style selector
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cardStyles) { style ->
                    FilterChip(
                        selected = style.name == selectedStyle.name,
                        onClick = { selectedStyle = style },
                        label = { Text("${style.emoji} ${style.name}", style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card preview
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .heightIn(min = 300.dp, max = 500.dp),
                shape = RoundedCornerShape(24.dp),
                color = selectedStyle.primaryColor,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Card header
                    Text(
                        text = "AnimeAI \uD83D\uDC95",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = selectedStyle.secondaryColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(1.dp),
                        color = selectedStyle.secondaryColor.copy(alpha = 0.2f)
                    ) {}
                    Spacer(modifier = Modifier.height(12.dp))

                    // Card content
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black.copy(alpha = 0.8f),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Watermark
                    Text(
                        text = "via AnimeAI · 截图分享卡片",
                        style = MaterialTheme.typography.labelSmall,
                        color = selectedStyle.secondaryColor.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content editor
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .heightIn(max = 120.dp),
                shape = RoundedCornerShape(12.dp),
                label = { Text("编辑分享内容") },
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { showShareOptions = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("分享卡片")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showShareOptions) {
        AlertDialog(
            onDismissRequest = { showShareOptions = false },
            title = { Text("分享到") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { showShareOptions = false }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("保存到相册")
                    }
                    TextButton(onClick = { showShareOptions = false }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("分享到其他应用")
                    }
                    TextButton(onClick = { showShareOptions = false }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("复制到剪贴板")
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showShareOptions = false }) { Text("取消") } }
        )
    }
}
