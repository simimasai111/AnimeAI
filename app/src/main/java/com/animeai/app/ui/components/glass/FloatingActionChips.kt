package com.animeai.app.ui.components.glass

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FloatingActionChips(
    visible: Boolean,
    onDismiss: () -> Unit,
    onArena: () -> Unit,
    onCrossValidate: () -> Unit,
    onBranch: () -> Unit,
    onShare: () -> Unit,
    onTemplate: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionChipSmall(icon = Icons.Filled.Compare, label = "竞技场", onClick = onArena)
                    ActionChipSmall(icon = Icons.Filled.CompareArrows, label = "交叉验证", onClick = onCrossValidate)
                    ActionChipSmall(icon = Icons.Filled.AccountTree, label = "分支", onClick = onBranch)
                    ActionChipSmall(icon = Icons.Filled.Share, label = "分享", onClick = onShare)
                    ActionChipSmall(icon = Icons.Filled.Book, label = "模板", onClick = onTemplate)
                }
            }
        }
    }
}

@Composable
private fun ActionChipSmall(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp)) },
        shape = RoundedCornerShape(12.dp)
    )
}
