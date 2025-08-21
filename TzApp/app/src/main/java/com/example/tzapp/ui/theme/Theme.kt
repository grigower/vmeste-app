package com.example.tzapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = LightBluePrimary,
    onPrimary = Color.White,
    secondary = BeigeSecondary,
    onSecondary = TextDark,
    background = WhiteBackground,
    onBackground = TextDark,
    surface = WhiteBackground,
    onSurface = TextDark,
    error = RedEmergency
)

private val DarkColors = darkColorScheme(
    primary = LightBluePrimary,
    secondary = BeigeSecondary,
    error = RedEmergency
)

@Composable
fun TzAppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

