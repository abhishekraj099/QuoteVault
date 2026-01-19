package com.example.quotevault.presentation.auth.signup

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.quotevault.core.util.UiEvent

// 🎨 Premium Aura Color Palette (Same as Splash & Login)
private val ElectricPurple = Color(0xFF9333EA)
private val PureWhite = Color(0xFFFFFFFF)
private val LuminousWhite = Color(0xFFFAFAFA)
private val LightPurple = Color(0xFFE9D5FF)
private val SubtleGray = Color(0xFF94A3B8)
private val DarkSlate = Color(0xFF1E293B)
private val InputBackground = Color(0xFFF8FAFC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    // Logo rotation animation state
    var logoTapped by remember { mutableStateOf(false) }

    // Rotation animation: 0° → -45° → 0°
    val logoRotation by animateFloatAsState(
        targetValue = if (logoTapped) -45f else 0f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 300f
        ),
        label = "logo_rotation",
        finishedListener = {
            if (logoTapped) {
                logoTapped = false
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
                is UiEvent.NavigateBack -> {
                    navController.popBackStack()
                }
                else -> Unit
            }
        }
    }

    // 🎨 Premium SignUp UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LightPurple,
                        LuminousWhite
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(40.dp))

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

            Spacer(modifier = Modifier.height(32.dp))

            // 📝 Welcome Text
            Text(
                text = "Start Journey",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = DarkSlate,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "CREATE YOUR AURA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = SubtleGray,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 🎴 SignUp Card
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
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Name Label
                    Text(
                        text = "NAME",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SubtleGray,
                        letterSpacing = 2.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    // Name Field
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.onEvent(SignUpEvent.NameChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Your Name",
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

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
                        onValueChange = { viewModel.onEvent(SignUpEvent.EmailChanged(it)) },
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

                    Spacer(modifier = Modifier.height(24.dp))

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
                        onValueChange = { viewModel.onEvent(SignUpEvent.PasswordChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { viewModel.onEvent(SignUpEvent.TogglePasswordVisibility) }) {
                                Icon(
                                    imageVector = if (state.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (state.isPasswordVisible) "Hide password" else "Show password",
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

                    Spacer(modifier = Modifier.height(24.dp))

                    // Confirm Password Label
                    Text(
                        text = "CONFIRM PASSCODE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SubtleGray,
                        letterSpacing = 2.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    // Confirm Password Field
                    var confirmPasswordVisible by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = state.confirmPassword,
                        onValueChange = { viewModel.onEvent(SignUpEvent.ConfirmPasswordChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { viewModel.onEvent(SignUpEvent.ToggleConfirmPasswordVisibility) }) {
                                Icon(
                                    imageVector = if (state.isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (state.isConfirmPasswordVisible) "Hide password" else "Show password",
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

                    Spacer(modifier = Modifier.height(32.dp))

                    // Ascend Button
                    Button(
                        onClick = { viewModel.onEvent(SignUpEvent.SignUp) },
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
                                text = "Ascend",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Login Link
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HAVE AN IDENTITY? ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SubtleGray,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "ENTRY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricPurple,
                            letterSpacing = 1.sp,
                            modifier = Modifier.clickable {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
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

