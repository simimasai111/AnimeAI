package com.animeai.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.jeziellago.compose.markdown.Markdown

@Composable
fun MarkdownContent(
    content: String,
    modifier: Modifier = Modifier
) {
    Markdown(
        markdown = content,
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        color = MaterialTheme.colorScheme.onSurface,
        isTextSelectable = true
    )
}
