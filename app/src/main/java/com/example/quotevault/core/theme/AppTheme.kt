package com.example.quotevault.core.theme

import androidx.compose.ui.graphics.Color

/**
 * App Theme Data Class with modern, beautiful color schemes
 */
sealed class AppTheme(
    val name: String,
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val onPrimary: Color,
    val onSecondary: Color,
    val onBackground: Color,
    val onSurface: Color
) {
    // Default - Aura Purple (Matches reference image exactly)
    object Default : AppTheme(
        name = "Default",
        primary = AuraPurple,            // Vibrant purple for buttons/cards (#8B5CF6)
        secondary = AuraPurpleLight,     // Light purple for accents
        background = AuraLavenderBg,     // Very light lavender background (#F5F3FF)
        surface = AuraLavenderLight,     // Light lavender for cards (#FAF5FF)
        onPrimary = Color.White,         // White text on purple buttons
        onSecondary = AuraPurpleDark,    // Dark purple text
        onBackground = AuraPurpleDark,   // Dark purple text on page
        onSurface = AuraPurpleDark       // Dark purple text on cards
    )

    // Ocean - Refreshing Blue
    object Ocean : AppTheme(
        name = "Ocean",
        primary = OceanPrimary,
        secondary = OceanSecondary,
        background = Color.White,
        surface = Color(0xFFF0F9FF),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color(0xFF0C4A6E),
        onSurface = Color(0xFF0C4A6E)
    )

    // Forest - Calming Green
    object Forest : AppTheme(
        name = "Forest",
        primary = ForestPrimary,
        secondary = ForestSecondary,
        background = Color.White,
        surface = Color(0xFFF0FDF4),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color(0xFF064E3B),
        onSurface = Color(0xFF064E3B)
    )

    // Sunset - Warm Orange
    object Sunset : AppTheme(
        name = "Sunset",
        primary = SunsetPrimary,
        secondary = SunsetSecondary,
        background = Color.White,
        surface = Color(0xFFFFF7ED),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color(0xFF7C2D12),
        onSurface = Color(0xFF7C2D12)
    )

    // Midnight - Deep Purple
    object Midnight : AppTheme(
        name = "Midnight",
        primary = MidnightPrimary,
        secondary = MidnightSecondary,
        background = Color(0xFF0F172A), // Dark slate
        surface = Color(0xFF1E293B), // Darker slate
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color(0xFFF1F5F9), // Light slate
        onSurface = Color(0xFFF1F5F9)
    )

    // Lavender - NEW: Soft Purple/Pink
    object Lavender : AppTheme(
        name = "Lavender",
        primary = LavenderPrimary,
        secondary = LavenderSecondary,
        background = Color.White,
        surface = Color(0xFFFAF5FF),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color(0xFF581C87),
        onSurface = Color(0xFF581C87)
    )

    companion object {
        fun fromName(name: String): AppTheme {
            return when (name) {
                "Ocean" -> Ocean
                "Forest" -> Forest
                "Sunset" -> Sunset
                "Midnight" -> Midnight
                "Lavender" -> Lavender
                else -> Default
            }
        }

        fun getAllThemes(): List<AppTheme> {
            return listOf(Default, Ocean, Forest, Sunset, Midnight, Lavender)
        }
    }
}

