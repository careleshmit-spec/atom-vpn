package com.atom.vpn.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AtomDarkColorScheme = darkColorScheme(
    primary = AtomColors.Cyan,
    onPrimary = AtomColors.Background,
    primaryContainer = AtomColors.CyanGlow,
    onPrimaryContainer = AtomColors.Cyan,
    secondary = AtomColors.Purple,
    onSecondary = AtomColors.White,
    secondaryContainer = AtomColors.PurpleGlow,
    onSecondaryContainer = AtomColors.Purple,
    tertiary = AtomColors.SecureGreen,
    onTertiary = AtomColors.Background,
    error = AtomColors.Error,
    onError = AtomColors.White,
    errorContainer = AtomColors.ErrorGlow,
    onErrorContainer = AtomColors.Error,
    background = AtomColors.Background,
    onBackground = AtomColors.TextPrimary,
    surface = AtomColors.Surface,
    onSurface = AtomColors.TextPrimary,
    surfaceVariant = AtomColors.SurfaceVariant,
    onSurfaceVariant = AtomColors.TextSecondary,
    outline = AtomColors.CardBorder,
    outlineVariant = AtomColors.Divider,
    scrim = Color(0x80000000)
)

@Composable
fun AtomTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AtomDarkColorScheme,
        typography = AtomTypography,
        content = content
    )
}
