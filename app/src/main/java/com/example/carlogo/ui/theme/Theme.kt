package com.example.carlogo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ColorOutline = SoftBlue.copy(alpha = 0.42f)

private val CarDarkScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = MistWhite,
    primaryContainer = CardNavyLight,
    onPrimaryContainer = MistWhite,
    secondary = CyanGlow,
    onSecondary = NightSky,
    secondaryContainer = CardNavy,
    onSecondaryContainer = MistWhite,
    background = NightSky,
    onBackground = MistWhite,
    surface = DeepNavy,
    onSurface = MistWhite,
    surfaceVariant = CardNavy,
    onSurfaceVariant = SoftBlue,
    outline = ColorOutline,
    error = ErrorPink,
    onError = MistWhite,
)

@Composable
fun CarLogoTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = CarDarkScheme, typography = Typography, content = content)
}
