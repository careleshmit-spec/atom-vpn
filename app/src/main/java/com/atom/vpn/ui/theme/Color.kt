package com.atom.vpn.ui.theme

import androidx.compose.ui.graphics.Color

object AtomColors {
    // Backgrounds
    val Background = Color(0xFF05060A)
    val Surface = Color(0xFF0A0D1A)
    val SurfaceVariant = Color(0xFF0D1120)
    val SurfaceElevated = Color(0xFF111827)
    val Card = Color(0xFF0E1422)
    val CardBorder = Color(0xFF1A2340)

    // Primary — Electric Cyan
    val Cyan = Color(0xFF00D4FF)
    val CyanDim = Color(0xFF0099BB)
    val CyanGlow = Color(0x4000D4FF)
    val CyanBorder = Color(0x8000D4FF)

    // Secondary — Electric Blue
    val Blue = Color(0xFF0080FF)
    val BlueDim = Color(0xFF0055AA)
    val BlueGlow = Color(0x400080FF)

    // Accent — Purple / Magenta
    val Purple = Color(0xFFAA00FF)
    val PurpleDim = Color(0xFF7700BB)
    val PurpleGlow = Color(0x40AA00FF)
    val Magenta = Color(0xFFFF00CC)
    val MagentaGlow = Color(0x40FF00CC)

    // State — Connected / Secure (Neon Green)
    val SecureGreen = Color(0xFF00FF88)
    val SecureGreenDim = Color(0xFF00BB66)
    val SecureGreenGlow = Color(0x4000FF88)

    // State — Error / Disconnected
    val Error = Color(0xFFFF4444)
    val ErrorDim = Color(0xFFCC2222)
    val ErrorGlow = Color(0x40FF4444)
    val Warning = Color(0xFFFF8C00)
    val WarningGlow = Color(0x40FF8C00)

    // Text
    val TextPrimary = Color(0xFFE8EAF0)
    val TextSecondary = Color(0xFF8899BB)
    val TextTertiary = Color(0xFF445577)
    val TextOnDark = Color(0xFFFFFFFF)

    // Misc
    val Transparent = Color(0x00000000)
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)
    val Divider = Color(0xFF1A2340)

    // Gradients helpers
    val GradientCyanBlue = listOf(Cyan, Blue)
    val GradientPurpleMagenta = listOf(Purple, Magenta)
    val GradientSecure = listOf(SecureGreen, Cyan)
    val GradientError = listOf(Error, Warning)
}
