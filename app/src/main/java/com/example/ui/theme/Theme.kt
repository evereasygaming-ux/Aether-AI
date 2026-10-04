package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HologramColorScheme = darkColorScheme(
    primary = HoloCyan,
    onPrimary = HoloVoidBlack,
    primaryContainer = HoloDarkSurface,
    onPrimaryContainer = HoloCyan,
    secondary = HoloViolet,
    onSecondary = Color.White,
    secondaryContainer = HoloCardElevated,
    onSecondaryContainer = HoloViolet,
    tertiary = HoloTeal,
    onTertiary = HoloVoidBlack,
    background = HoloVoidBlack,
    onBackground = TextHoloPrimary,
    surface = HoloDarkSurface,
    onSurface = TextHoloPrimary,
    surfaceVariant = HoloCardSurface,
    onSurfaceVariant = TextHoloSecondary,
    outline = HoloCyanDim,
    error = HoloAlertRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HologramColorScheme,
        typography = Typography,
        content = content
    )
}
