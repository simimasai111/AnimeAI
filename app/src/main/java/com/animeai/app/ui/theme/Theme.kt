package com.animeai.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Pink500,
    onPrimary = White,
    primaryContainer = Pink100,
    onPrimaryContainer = Pink900,
    secondary = Lavender500,
    onSecondary = White,
    secondaryContainer = Lavender100,
    onSecondaryContainer = Lavender900,
    tertiary = RoseQuartz,
    onTertiary = Gray900,
    background = Pink50,
    onBackground = Gray900,
    surface = White,
    onSurface = Gray900,
    surfaceVariant = Gray50,
    onSurfaceVariant = Gray700,
    outline = Gray400,
    outlineVariant = Gray200
)

private val DarkColorScheme = darkColorScheme(
    primary = Pink300,
    onPrimary = Pink900,
    primaryContainer = Pink700,
    onPrimaryContainer = Pink100,
    secondary = Lavender300,
    onSecondary = Lavender900,
    secondaryContainer = Lavender700,
    onSecondaryContainer = Lavender100,
    tertiary = RoseQuartz,
    onTertiary = Gray900,
    background = Gray900,
    onBackground = Gray100,
    surface = Gray800,
    onSurface = Gray100,
    surfaceVariant = Gray700,
    onSurfaceVariant = Gray300,
    outline = Gray500,
    outlineVariant = Gray600
)

@Composable
fun AnimeAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
