package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val AutoVibeDarkColorScheme = darkColorScheme(
    primary = ElectricLime,
    onPrimary = MatteBlack,
    primaryContainer = ElectricLimeDim,
    onPrimaryContainer = ElectricLime,
    secondary = BrightAmber,
    onSecondary = MatteBlack,
    secondaryContainer = BrightAmberDim,
    onSecondaryContainer = BrightAmber,
    tertiary = VerifiedGreen,
    onTertiary = MatteBlack,
    background = MatteBlack,
    onBackground = CrispWhite,
    surface = CharcoalSurface,
    onSurface = CrispWhite,
    surfaceVariant = SlateField,
    onSurfaceVariant = MutedText,
    outline = SubtleBorder,
    outlineVariant = SlateField,
    error = AlertRed,
    onError = CrispWhite,
    errorContainer = AlertRedDim,
    onErrorContainer = AlertRed
)

val AutoVibeShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun AutoVibeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AutoVibeDarkColorScheme,
        typography = AutoVibeTypography,
        shapes = AutoVibeShapes,
        content = content
    )
}
