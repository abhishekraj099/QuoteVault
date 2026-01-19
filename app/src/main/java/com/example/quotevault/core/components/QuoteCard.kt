package com.example.quotevault.core.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quotevault.domain.model.Quote

@Composable
fun QuoteCard(
    quote: Quote,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Tap animation
    var isPressed by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_scale"
    )

    // Auto-reset
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(cardScale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                    }
                )
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color.White // Clean white background
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Quote text
            Text(
                text = quote.text,
                fontSize = 17.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Italic,
                color = androidx.compose.ui.graphics.Color(0xFF212121), // Dark gray/black
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Author
            Text(
                text = "— ${quote.author.uppercase()}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color(0xFF8B5CF6), // Purple
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Bottom row - actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Save button
                Surface(
                    onClick = onFavoriteClick,
                    shape = RoundedCornerShape(16.dp),
                    color = if (quote.isFavorite)
                        androidx.compose.ui.graphics.Color(0xFF8B5CF6) // Purple filled for saved
                    else
                        androidx.compose.ui.graphics.Color(0xFFF5F5F5) // Light gray for unsaved
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = if (quote.isFavorite)
                                Icons.Default.Favorite
                            else
                                Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (quote.isFavorite)
                                androidx.compose.ui.graphics.Color.White // White icon on purple
                            else
                                androidx.compose.ui.graphics.Color(0xFF757575), // Gray for unsaved
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SAVE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (quote.isFavorite)
                                androidx.compose.ui.graphics.Color.White // White text on purple
                            else
                                androidx.compose.ui.graphics.Color(0xFF757575) // Gray for unsaved
                        )
                    }
                }

                // Share button
                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            androidx.compose.ui.graphics.Color(0xFFF5F5F5), // Light gray
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        tint = androidx.compose.ui.graphics.Color(0xFF757575), // Gray
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
