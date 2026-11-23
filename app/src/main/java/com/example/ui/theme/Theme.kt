package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GlassDarkColorScheme = darkColorScheme(
    primary = CyanPulse,
    onPrimary = AbyssNavy,
    secondary = ElectricViolet,
    onSecondary = IceWhite,
    tertiary = PrismMagenta,
    onTertiary = AbyssNavy,
    background = AbyssNavy,
    onBackground = IceWhite,
    surface = DeepSlate,
    onSurface = IceWhite,
    surfaceVariant = MidnightSurface,
    onSurfaceVariant = SoftFrost
)

@Composable
fun GlassLabTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GlassDarkColorScheme,
        typography = GlassLabTypography,
        content = content
    )
}
