package com.example.quotevault.presentation.auth.login

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.quotevault.core.navigation.Screen
import com.example.quotevault.core.util.UiEvent

// 🎨 Premium Aura Color Palette (Exact match to reference)
private val ElectricPurple = Color(0xFF9333EA)       // Primary purple for buttons/accents
private val PureWhite = Color(0xFFFFFFFF)            // Card background
private val LuminousWhite = Color(0xFFF5F3FF)        // Very light lavender
private val LightPurple = Color(0xFFE9D5FF)          // Light purple for gradient top
private val SubtleGray = Color(0xFF94A3B8)           // Labels and secondary text
private val DarkSlate = Color(0xFF0F172A)            // Main heading text (darker)
private val InputBackground = Color(0xFFF8FAFC)      // Input field background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Logo rotation animation state
    var logoTapped by remember { mutableStateOf(false) }

    // Rotation animation: 0Â° â†’ -45Â° â†’ 0Â° (smooth rotation from center)
    val logoRotation by animateFloatAsState(
        targetValue = if (logoTapped) -45f else 0f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 300f
        ),
        label = "logo_rotation",
        finishedListener = {
            if (logoTapped) {
                logoTapped = false // Reset to rotate back
            }
        }
    )

    // Handle UI events
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is UiEvent.Navigate -> {
                    navController.navigate(event.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
                is UiEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    // ðŸŽ¨ Premium Login UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LightPurple,      // Top: Light purple
                        LuminousWhite     // Bottom: Off-white
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // ✨ Tappable Logo with Rotation Animation
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .rotate(logoRotation)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        logoTapped = true
                    }
            ) {
                // Outer glow
                Box(
                    modifier = Modifier
                        .size(160.dp)
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

                // Icon container
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = ElectricPurple.copy(alpha = 0.3f),
                            spotColor = ElectricPurple.copy(alpha = 0.3f)
                        )
                        .background(
                            ElectricPurple,
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = "Aura Logo",
                        tint = PureWhite,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🏛 Welcome Text
            Text(
                text = "Welcome Back",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = DarkSlate,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "YOUR SANCTUARY AWAITS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = SubtleGray,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 🎴 Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PureWhite
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Identity Label
                    Text(
                        text = "IDENTITY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SubtleGray,
                        letterSpacing = 2.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    // Email Field
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "name@wisdom.com",
                                color = SubtleGray.copy(alpha = 0.5f)
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBackground,
                            unfocusedContainerColor = InputBackground,
                            focusedBorderColor = ElectricPurple,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = DarkSlate,
                            unfocusedTextColor = DarkSlate
                        ),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Label
                    Text(
                        text = "PASSCODE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SubtleGray,
                        letterSpacing = 2.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    // Password Field
                    var passwordVisible by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = SubtleGray
                                )
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBackground,
                            unfocusedContainerColor = InputBackground,
                            focusedBorderColor = ElectricPurple,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = DarkSlate,
                            unfocusedTextColor = DarkSlate
                        ),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Forgot Password Link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Forgot Password?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ElectricPurple,
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.ResetPassword.route)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Illuminate Button
                    Button(
                        onClick = { viewModel.onEvent(LoginEvent.Login) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricPurple
                        ),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                color = PureWhite,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = "Illuminate",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Divider with OR text
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = SubtleGray.copy(alpha = 0.3f)
                        )
                        Text(
                            text = "OR",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = SubtleGray.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 16.dp),
                            letterSpacing = 2.sp
                        )
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = SubtleGray.copy(alpha = 0.3f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Create Account Button (more prominent)
                    OutlinedButton(
                        onClick = {
                            navController.navigate(Screen.SignUp.route)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = ElectricPurple  // Changed to ElectricPurple for better visibility
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            ElectricPurple
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Create Account",
                            tint = ElectricPurple,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Create New Account",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricPurple  // Changed to ElectricPurple for better visibility
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        // Snackbar Host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}
