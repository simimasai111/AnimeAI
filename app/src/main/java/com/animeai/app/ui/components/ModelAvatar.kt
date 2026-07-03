package com.animeai.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.animeai.app.R
import com.animeai.app.data.model.Persona
import com.animeai.app.util.ModelIconMapper

@Composable
fun ModelAvatar(
    modelName: String?,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp
) {
    val iconInfo = ModelIconMapper.getModelIcon(modelName ?: "")
    val drawableRes = iconInfo.drawableResId
    val bgColor = iconInfo.backgroundColor
    val displayName = iconInfo.displayName

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size / 3))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        if (drawableRes != R.drawable.ic_model_default || modelName.isNullOrBlank()) {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = displayName,
                modifier = Modifier
                    .padding(size * 0.15f)
                    .fillMaxSize()
            )
        } else {
            Text(
                text = modelName.take(2).uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = (size.value / 2.5).sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PersonaOrModelAvatar(
    persona: Persona?,
    modelName: String? = null,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp
) {
    if (modelName != null && modelName.isNotBlank()) {
        ModelAvatar(
            modelName = modelName,
            modifier = modifier,
            size = size
        )
    } else if (persona != null) {
        PersonaAvatar(
            persona = persona,
            modifier = modifier,
            size = size
        )
    }
}

@Composable
fun ModelInfoBadge(
    modelName: String?,
    modifier: Modifier = Modifier
) {
    if (modelName.isNullOrBlank()) return

    val iconInfo = ModelIconMapper.getModelIcon(modelName)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(iconInfo.backgroundColor.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            painter = painterResource(id = iconInfo.drawableResId),
            contentDescription = null,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = iconInfo.displayName.ifEmpty { modelName },
            style = MaterialTheme.typography.labelSmall,
            color = iconInfo.backgroundColor
        )
    }
}
