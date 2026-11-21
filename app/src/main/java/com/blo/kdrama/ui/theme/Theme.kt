package com.blo.kdrama.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Pink = Color(0xFFE0457B)
val PinkDark = Color(0xFFFF8FB3)
val Violet = Color(0xFF7C4DFF)
val Mint = Color(0xFF2BB673)
val Coral = Color(0xFFE5533D)

private val Light = lightColorScheme(
    primary = Pink,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9E3),
    onPrimaryContainer = Color(0xFF3E0019),
    secondary = Violet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8DDFF),
    onSecondaryContainer = Color(0xFF21005D),
    tertiary = Mint,
    tertiaryContainer = Color(0xFFD5F5E3),
    onTertiaryContainer = Color(0xFF00391C),
    background = Color(0xFFFFF7FA),
    surface = Color(0xFFFFF7FA),
    surfaceVariant = Color(0xFFF6E6EC),
    surfaceContainer = Color(0xFFFCEEF3),
    surfaceContainerHigh = Color(0xFFF8E7ED),
    error = Coral,
)

private val Dark = darkColorScheme(
    primary = PinkDark,
    onPrimary = Color(0xFF5E1130),
    primaryContainer = Color(0xFF7D2947),
    onPrimaryContainer = Color(0xFFFFD9E3),
    secondary = Color(0xFFCFBCFF),
    onSecondary = Color(0xFF381E72),
    secondaryContainer = Color(0xFF4F378A),
    onSecondaryContainer = Color(0xFFE8DDFF),
    tertiary = Color(0xFF7FDBA8),
    tertiaryContainer = Color(0xFF00522B),
    onTertiaryContainer = Color(0xFFD5F5E3),
    background = Color(0xFF17121A),
    surface = Color(0xFF17121A),
    surfaceVariant = Color(0xFF2E2530),
    surfaceContainer = Color(0xFF221B25),
    surfaceContainerHigh = Color(0xFF2B232E),
    error = Color(0xFFFF8A78),
)

private val AppTypography = Typography(
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 32.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 21.sp, lineHeight = 27.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 23.sp),
)

@Composable
fun BloTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) Dark else Light,
        typography = AppTypography,
        content = content,
    )
}
