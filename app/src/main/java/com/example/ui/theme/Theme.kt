package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val FuturisticMidnightColorScheme = darkColorScheme(
    primary = ElectricBlueBright,
    onPrimary = CrispWhite,
    primaryContainer = RoyalBlueDeep,
    onPrimaryContainer = CrispWhite,
    secondary = LuminousPurple,
    onSecondary = CrispWhite,
    secondaryContainer = NeonOrangeContainer,
    onSecondaryContainer = CrispWhite,
    tertiary = ElectricCyan,
    onTertiary = Color(0xFF031424),
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

private val DaylightColorScheme = lightColorScheme(
    primary = LightPrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = LightSecondaryViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF4C1D95),
    tertiary = Color(0xFF0891B2),
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    error = Color(0xFFDC2626),
    onError = Color.White
)

val AdityaShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

@Composable
fun AdityaOnlineTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) FuturisticMidnightColorScheme else DaylightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AdityaShapes,
        content = content
    )
}
