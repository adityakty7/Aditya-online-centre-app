package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AdityaCyberDarkColorScheme = darkColorScheme(
    primary = NeonOrange,
    onPrimary = OnNeonOrange,
    primaryContainer = NeonOrangeContainer,
    onPrimaryContainer = NeonOrangeLight,
    secondary = BrushedSilver,
    onSecondary = ObsidianBlack,
    secondaryContainer = CharcoalElevated,
    onSecondaryContainer = CrispWhite,
    tertiary = ElectricCyan,
    onTertiary = ObsidianBlack,
    tertiaryContainer = CyanContainer,
    onTertiaryContainer = ElectricCyan,
    background = ObsidianBlack,
    onBackground = CrispWhite,
    surface = CharcoalSurface,
    onSurface = CrispWhite,
    surfaceVariant = CharcoalElevated,
    onSurfaceVariant = BrushedSilver,
    outline = CharcoalCardBorder,
    error = StatusErrorRed,
    onError = Color.White
)

private val AdityaDaylightBlueColorScheme = lightColorScheme(
    primary = CyberBluePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4FF),
    onPrimaryContainer = Color(0xFF001B3D),
    secondary = NeonOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDBC8),
    onSecondaryContainer = Color(0xFF331200),
    tertiary = CyberBlueSecondary,
    onTertiary = Color.White,
    background = DaylightBackground,
    onBackground = DaylightOnSurface,
    surface = DaylightSurface,
    onSurface = DaylightOnSurface,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF334155),
    outline = Color(0xFFCBD5E1),
    error = Color(0xFFD32F2F),
    onError = Color.White
)

@Composable
fun AdityaOnlineTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AdityaCyberDarkColorScheme else AdityaDaylightBlueColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
