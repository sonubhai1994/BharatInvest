package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CleanPrimaryDark,
    secondary = CleanSecondaryDark,
    tertiary = CleanTertiaryDark,
    background = CleanBgDark,
    surface = CleanSurfaceDark,
    surfaceVariant = CleanSurfaceVariantDark,
    onBackground = CleanOnBackgroundDark,
    onSurface = CleanOnBackgroundDark,
    primaryContainer = Color(0xFF31105D),
    onPrimaryContainer = CleanPrimaryDark,
    outline = CleanBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = CleanPrimaryPurple,
    secondary = CleanSecondaryPurple,
    tertiary = CleanTertiaryPurple,
    background = CleanBgLight,
    surface = CleanSurfaceLight,
    surfaceVariant = CleanSurfaceVariantLight,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    primaryContainer = CleanLavenderHighlight,
    onPrimaryContainer = CleanSecondaryPurple,
    outline = CleanBorderLight,
    outlineVariant = CleanBorderSubtle
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to keep our signature Indian financial colors consistent
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
