package com.example.quotevault.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// 🎨 Premium Aura Color Palette (Minimal & Elegant)
private val ElectricPurple = Color(0xFF9333EA) // Main brand color
private val PureWhite = Color(0xFFFFFFFF)
private val LuminousWhite = Color(0xFFFAFAFA)
private val SubtleGray = Color(0xFF94A3B8)

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    // Animation states
    var startAnimation by remember { mutableStateOf(false) }

    // 1. Icon Entrance Animation: Scale 0.8 → 1.2 → 1.0 with smooth easing
    val iconScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = 0.6f, // Creates overshoot to 1.2
            stiffness = 200f
        ),
        label = "icon_scale"
    )

    // 2. Gentle Aura Pulse - Looping every 3 seconds
    val infiniteTransition = rememberInfiniteTransition(label = "aura_pulse")
    val auraPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_pulse"
    )

    // 3. App Name Slide Up & Fade In (with delay)
    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(800, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "text_fade"
    )

    val textOffset by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 30f,
        animationSpec = tween(800, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "text_slide"
    )

    // Launch sequence
    LaunchedEffect(Unit) {
        delay(100) // Brief pause
        startAnimation = true
        delay(3000) // Hold the reveal
        onSplashComplete()
    }

    // 🎨 Minimal Premium Layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PureWhite,
                        LuminousWhite
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // ✨ App Icon with Natural Glowing Aura
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Outer soft glow layer (furthest, most diffused)
                Box(
                    modifier = Modifier
                        .size(220.dp * auraPulse)
                        .blur(60.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    ElectricPurple.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Middle glow layer
                Box(
                    modifier = Modifier
                        .size(160.dp * auraPulse)
                        .blur(40.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    ElectricPurple.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Inner glow layer (closest to icon)
                Box(
                    modifier = Modifier
                        .size(140.dp * auraPulse)
                        .blur(25.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    ElectricPurple.copy(alpha = 0.25f),
                                    ElectricPurple.copy(alpha = 0.05f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Rounded-square icon with subtle shadow
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(iconScale)
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(28.dp),
                            ambientColor = ElectricPurple.copy(alpha = 0.4f),
                            spotColor = ElectricPurple.copy(alpha = 0.4f)
                        )
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    ElectricPurple,
                                    ElectricPurple.copy(alpha = 0.95f)
                                )
                            ),
                            shape = RoundedCornerShape(28.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = "Aura Logo",
                        tint = PureWhite,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 📝 App Name - Slide up and fade in
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(y = textOffset.dp)
                    .alpha(textAlpha)
            ) {
                // "Aura" - Large elegant serif font
                Text(
                    text = "Aura",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.Serif, // Elegant serif
                    color = Color(0xFF1E293B), // Dark slate
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // "WISDOM VAULT" - Small uppercase with wide spacing
                Text(
                    text = "WISDOM VAULT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = SubtleGray,
                    letterSpacing = 5.sp, // Wide letter spacing
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(1.5f))
        }
    }
}

