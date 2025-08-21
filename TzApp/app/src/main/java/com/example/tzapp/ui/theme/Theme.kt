package com.example.tzapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
	primary = Primary,
	secondary = Secondary,
	tertiary = Tertiary
)

private val DarkColors = darkColorScheme(
	primary = Primary,
	secondary = Secondary,
	tertiary = Tertiary
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

