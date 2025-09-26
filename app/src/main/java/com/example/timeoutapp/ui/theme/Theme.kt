package com.example.timeoutapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light Theme Colors
private val LightPrimary = Color(0xFF6200EE)
private val LightOnPrimary = Color(0xFFFFFFFF)
private val LightBackground = Color(0xFFFFFBFE)
private val LightSurface = Color(0xFFFFFBFE)

// Dark Theme Colors
private val DarkPrimary = Color(0xFFBB86FC)
private val DarkOnPrimary = Color(0xFF000000)
private val DarkBackground = Color(0xFF121212)
private val DarkSurface = Color(0xFF121212)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    background = LightBackground,
    surface = LightSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    background = DarkBackground,
    surface = DarkSurface
)

@Composable
fun TimeOutAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}