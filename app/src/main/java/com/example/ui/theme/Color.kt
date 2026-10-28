package com.example.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ============================================================================
// PREMIUM FUTURISTIC PALETTE — ADITYA ONLINE CENTER
// Deep Midnight Navy, Electric Blue, Vivid Violet/Purple, and Luminous Cyan
// ============================================================================

// Core Midnight Navy & Deep Blue-Black Surfaces
val ObsidianBlack = Color(0xFF040814)          // Deepest Midnight Navy-Black background
val MidnightNavy = Color(0xFF060C1F)           // Rich Midnight Navy backdrop
val CharcoalSurface = Color(0xFF0B142C)        // Deep Blue-Black primary card surface
val CharcoalElevated = Color(0xFF121F42)       // Elevated Blue-Navy container surface
val CharcoalCardBorder = Color(0xFF1E3266)     // Subtle Electric Blue-Navy border

// Primary & Secondary Futuristic Accents
val ElectricBlue = Color(0xFF2563EB)           // Primary Electric Blue
val ElectricBlueBright = Color(0xFF3B82F6)     // Luminous Electric Blue highlight
val ElectricBlueGlow = Color(0xFF60A5FA)       // Soft Sky-Blue edge highlight

val VividViolet = Color(0xFF7C3AED)            // Secondary Vivid Violet
val VividPurple = Color(0xFF9333EA)            // Rich Neon Purple
val LuminousPurple = Color(0xFFA855F7)         // Luminous Violet-Purple accent

val LuminousCyan = Color(0xFF06B6D4)           // Cyan primary highlight
val ElectricCyan = Color(0xFF22D3EE)           // Luminous Cyan glow

// Aliased Semantic Tokens (Preserves existing imports while upgrading to Electric Blue / Purple / Cyan)
val RoyalBlueAccent = ElectricBlue
val RoyalBlueBright = ElectricBlueBright
val RoyalBlueDeep = Color(0xFF172554)          // Deep Indigo-Blue container
val NeonOrange = LuminousPurple                // Upgraded from orange to Vivid Purple-Violet for cohesive futuristic UI
val NeonOrangeBright = Color(0xFFC084FC)       // Soft Lavender-Purple highlight
val NeonOrangeContainer = Color(0xFF2E1065)    // Deep Violet container

// Typography & Neutral Tokens
val CrispWhite = Color(0xFFF8FAFC)             // Soft White primary text
val BrushedSilver = Color(0xFFCBD5E1)          // Cool Silver secondary text
val MutedSlate = Color(0xFF94A3B8)             // Muted Silver-Blue captions

// Semantic Order & Verification Status Tokens
val StatusVerifiedGreen = Color(0xFF10B981)
val StatusPendingAmber = Color(0xFFF59E0B)
val StatusProcessingBlue = Color(0xFF38BDF8)
val StatusErrorRed = Color(0xFFF43F5E)

// Light Mode Fallback Tokens (Futuristic Ice-Blue & Royal Indigo)
val LightBackground = Color(0xFFF1F5F9)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFE2E8F0)
val LightPrimaryBlue = Color(0xFF1D4ED8)
val LightSecondaryViolet = Color(0xFF6D28D9)
val LightOnSurface = Color(0xFF0F172A)
val LightOnSurfaceVariant = Color(0xFF475569)
val LightOutline = Color(0xFFCBD5E1)

// ============================================================================
// REUSABLE FUTURISTIC GRADIENT BRUSHES
// ============================================================================

val BlueToPurpleGradientBrush = Brush.horizontalGradient(
    colors = listOf(
        ElectricBlue,
        VividViolet,
        LuminousPurple
    )
)

val PurpleToCyanGradientBrush = Brush.horizontalGradient(
    colors = listOf(
        VividViolet,
        ElectricBlueBright,
        ElectricCyan
    )
)

val CyanToBlueGradientBrush = Brush.horizontalGradient(
    colors = listOf(
        LuminousCyan,
        ElectricBlueBright
    )
)

val FuturisticCardBorderBrush = Brush.linearGradient(
    colors = listOf(
        ElectricCyan.copy(alpha = 0.65f),
        ElectricBlueBright.copy(alpha = 0.45f),
        LuminousPurple.copy(alpha = 0.60f)
    ),
    start = Offset(0f, 0f),
    end = Offset(600f, 400f)
)

val SubtleBlueBorderBrush = Brush.linearGradient(
    colors = listOf(
        ElectricBlueBright.copy(alpha = 0.45f),
        CharcoalCardBorder.copy(alpha = 0.75f),
        VividViolet.copy(alpha = 0.40f)
    )
)
