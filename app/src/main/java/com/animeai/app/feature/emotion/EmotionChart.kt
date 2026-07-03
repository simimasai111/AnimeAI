package com.animeai.app.feature.emotion

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun EmotionChart(
    analysis: EmotionAnalysis?,
    modifier: Modifier = Modifier
) {
    if (analysis == null || analysis.points.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            analysis.dominantEmotions.take(3).forEach { emotion ->
                val match = emotionColorMap.find { it.emotion == emotion }
                Text(
                    text = "${match?.emoji ?: ""} $emotion",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            val width = size.width
            val height = size.height
            val points = analysis.points
            if (points.isEmpty()) return@Canvas

            val stepX = width / (points.size - 1).coerceAtLeast(1)

            // Draw positive line
            val posPath = Path()
            points.forEachIndexed { index, point ->
                val x = index * stepX
                val y = height - (point.positiveScore * height)
                if (index == 0) posPath.moveTo(x, y) else posPath.lineTo(x, y)
            }
            drawPath(
                path = posPath,
                color = Color(0xFFFF6B81),
                style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Draw negative line
            val negPath = Path()
            points.forEachIndexed { index, point ->
                val x = index * stepX
                val y = height - (point.negativeScore * height)
                if (index == 0) negPath.moveTo(x, y) else negPath.lineTo(x, y)
            }
            drawPath(
                path = negPath,
                color = Color(0xFF5DADE2),
                style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Draw energy dots
            points.forEachIndexed { index, point ->
                val x = index * stepX
                val y = height - (point.energyLevel * height)
                drawCircle(
                    color = Color(0xFFFFD700),
                    radius = point.energyLevel * 6f + 2f,
                    center = Offset(x, y)
                )
            }
        }
    }
}
