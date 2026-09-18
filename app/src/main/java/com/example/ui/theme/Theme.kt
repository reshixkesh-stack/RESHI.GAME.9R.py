package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MathIndigoPrimary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = MathEmerald,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    tertiary = MathAmber,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color.White
)

private val LightColorScheme = darkColorScheme(
    primary = MathIndigoSecondary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = MathEmerald,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    tertiary = MathAmber,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(colorScheme = DarkColorScheme, typography = Typography, content = content)
}
