package com.atom.vpn.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Using system fonts with futuristic styling.
// In production replace with a custom typeface (e.g. Exo 2 or Space Grotesk).
object AtomFonts {
    val Default = FontFamily.Default
}

val AtomTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = AtomFonts.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
        color = AtomColors.TextPrimary
    ),
    displayMedium = TextStyle(
        fontFamily = AtomFonts.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
        color = AtomColors.TextPrimary
    ),
    headlineLarge = TextStyle(
        fontFamily = AtomFonts.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
        color = AtomColors.TextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = AtomFonts.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
        color = AtomColors.TextPrimary
    ),
    headlineSmall = TextStyle(
        fontFamily = AtomFonts.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
        color = AtomColors.TextPrimary
    ),
    titleLarge = TextStyle(
        fontFamily = AtomFonts.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        color = AtomColors.TextPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = AtomFonts.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
        color = AtomColors.TextPrimary
    ),
    titleSmall = TextStyle(
        fontFamily = AtomFonts.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        color = AtomColors.TextSecondary
    ),
    bodyLarge = TextStyle(
        fontFamily = AtomFonts.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = AtomColors.TextPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = AtomFonts.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        color = AtomColors.TextSecondary
    ),
    bodySmall = TextStyle(
        fontFamily = AtomFonts.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
        color = AtomColors.TextTertiary
    ),
    labelLarge = TextStyle(
        fontFamily = AtomFonts.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 1.5.sp,
        color = AtomColors.Cyan
    ),
    labelMedium = TextStyle(
        fontFamily = AtomFonts.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.2.sp,
        color = AtomColors.TextSecondary
    ),
    labelSmall = TextStyle(
        fontFamily = AtomFonts.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = AtomColors.TextTertiary
    )
)
