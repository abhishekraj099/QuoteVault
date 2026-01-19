package com.example.quotevault.core.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Suppress("DEPRECATION")
@Composable
fun QuoteVaultTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled by default to use appTheme
    appTheme: AppTheme = AppTheme.Default,
    fontSizePercent: Int = 100,
    content: @Composable () -> Unit
) {
    // Only use dynamic color if it's enabled AND the theme is Default
    val useDynamicColor = dynamicColor && appTheme.name == "Default" && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val colorScheme = when {
        useDynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme(
            primary = appTheme.primary,
            secondary = appTheme.secondary,
            background = appTheme.background,
            surface = appTheme.surface,
            onPrimary = appTheme.onPrimary,
            onSecondary = appTheme.onSecondary,
            onBackground = appTheme.onBackground,
            onSurface = appTheme.onSurface
        )
        else -> lightColorScheme(
            primary = appTheme.primary,
            secondary = appTheme.secondary,
            background = appTheme.background,
            surface = appTheme.surface,
            onPrimary = appTheme.onPrimary,
            onSecondary = appTheme.onSecondary,
            onBackground = appTheme.onBackground,
            onSurface = appTheme.onSurface
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            setStatusBarColorForView(view, colorScheme.primary.toArgb(), darkTheme)
        }
    }

    // Apply font size scaling
    val scaledTypography = scaleTypography(AppTypography, fontSizePercent)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = scaledTypography,
        content = content
    )
}

@Suppress("DEPRECATION")
private fun setStatusBarColorForView(view: android.view.View, color: Int, darkTheme: Boolean) {
    val window = (view.context as Activity).window
    window.statusBarColor = color
    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
}
