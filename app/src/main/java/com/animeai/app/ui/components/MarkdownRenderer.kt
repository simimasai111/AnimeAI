package com.animeai.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MarkdownContent(
    content: String,
    modifier: Modifier = Modifier
) {
    val lines = content.lines()
    Column(modifier = modifier.fillMaxWidth().padding(4.dp)) {
        lines.forEach { line ->
            val text = line.trimStart()
            when {
                text.startsWith("## ") -> Text(
                    text = text.removePrefix("## "),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                text.startsWith("# ") -> Text(
                    text = text.removePrefix("# "),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                text.startsWith("- ") || text.startsWith("* ") -> Text(
                    text = "  \u2022 ${text.removePrefix("- ").removePrefix("* ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                text.matches(Regex("^\\d+\\.\\s.*")) -> Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                else -> Text(
                    text = line,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
