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

private val LightColorScheme = lightColorScheme(
    primary = Ios.Blue,
    onPrimary = PolishBlueOnPrimary,
    primaryContainer = Ios.BlueTint,
    onPrimaryContainer = PolishBlueOnContainer,
    secondary = PolishBlueDark,
    onSecondary = PolishBlueOnPrimary,
    secondaryContainer = PolishEmeraldBg,
    onSecondaryContainer = PolishEmerald,
    tertiary = SafetyAmberTertiary,
    tertiaryContainer = SafetyAmberContainer,
    onTertiaryContainer = SafetyAmberOnContainer,
    background = Ios.Background,
    onBackground = Ios.Label,
    surface = PolishSurface,
    onSurface = PolishSlate900,
    surfaceVariant = PolishSlate100,
    onSurfaceVariant = PolishSlate600,
    outline = PolishSlate300,
    outlineVariant = PolishSlate200
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    secondary = PolishEmerald,
    onSecondary = PolishBlueOnPrimary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = PolishSlate800,
    onSurfaceVariant = PolishSlate400,
    outline = PolishSlate600
)

@Composable
fun HealthMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded health palette consistent
    content: @Composable () -> Unit
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
