package com.example.quotevault.presentation.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private const val TAG = "QV_SettingsScreen"

/**
 * Settings Screen - FULLY FUNCTIONAL
 * All settings persist and apply immediately
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRoute(
    onNavigateBack: (() -> Unit)? = null,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    Log.d(TAG, "SettingsRoute composing - viewModel: $viewModel")

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Log.d(TAG, "SettingsRoute uiState collected: $uiState")

    var showThemePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showPermissionDeniedDialog by remember { mutableStateOf(false) }
    val themes = listOf("Default", "Ocean", "Forest", "Sunset", "Midnight", "Lavender")

    // Check if battery optimization is ignored (background activity allowed)
    val powerManager = context.getSystemService(android.content.Context.POWER_SERVICE) as PowerManager
    var isIgnoringBatteryOptimizations by remember {
        mutableStateOf(powerManager.isIgnoringBatteryOptimizations(context.packageName))
    }

    // Notification permission launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Notification permission granted - enabling notifications")
            viewModel.enableNotifications()
        } else {
            Log.d(TAG, "Notification permission denied")
            showPermissionDeniedDialog = true
        }
    }

    // Function to open battery optimization settings
    fun openBatterySettings() {
        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to app settings
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
            } catch (e2: Exception) {
                Log.e(TAG, "Could not open settings", e2)
            }
        }
    }

    // Function to handle notification toggle with permission check
    fun handleNotificationToggle(currentlyEnabled: Boolean) {
        if (currentlyEnabled) {
            // User wants to disable - no permission needed
            viewModel.disableNotifications()
        } else {
            // User wants to enable - check permission first
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                when {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        Log.d(TAG, "Permission already granted, enabling notifications")
                        viewModel.enableNotifications()
                    }
                    else -> {
                        Log.d(TAG, "Requesting notification permission")
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            } else {
                // Pre-Android 13 doesn't need runtime permission
                viewModel.enableNotifications()
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF5F3FF),
                            Color(0xFFEDE9FE),
                            Color(0xFFE9D5FF)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Dark Mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Dark Mode", modifier = Modifier.align(Alignment.CenterVertically))
                Switch(
                    checked = uiState.isDarkMode,
                    onCheckedChange = { viewModel.toggleDarkMode() }
                )
            }

            // Font Size
            Text("Font Size: ${uiState.fontSize}%")
            Slider(
                value = uiState.fontSize.toFloat(),
                onValueChange = { newSize ->
                    viewModel.setFontSize(newSize.toInt())
                },
                valueRange = 80f..120f
            )

            HorizontalDivider()

            // Theme Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("App Theme")
                    Text(
                        uiState.theme,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(onClick = { showThemePicker = true }) {
                    Text("Change")
                }
            }

            HorizontalDivider()

            // Notifications Section Header
            Text(
                "Notifications",
                style = MaterialTheme.typography.titleMedium
            )

            // Notifications Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Daily Quote Notification")
                    Text(
                        "Get inspired with a new quote every day",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = uiState.isNotificationsEnabled,
                    onCheckedChange = {
                        handleNotificationToggle(uiState.isNotificationsEnabled)
                    }
                )
            }

            // Notification Time - only show when notifications are enabled
            if (uiState.isNotificationsEnabled) {

                // Battery Optimization Warning
                if (!isIgnoringBatteryOptimizations) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Background Activity Required",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Notifications won't work unless you allow background activity for this app.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    openBatterySettings()
                                    // Refresh status after returning
                                    isIgnoringBatteryOptimizations = powerManager.isIgnoringBatteryOptimizations(context.packageName)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Enable Background Activity")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTimePicker = true },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Notification Time")
                        Text(
                            formatTime(uiState.notificationTime),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = { showTimePicker = true }) {
                        Text("Change")
                    }
                }

                // Info text about notification
                Text(
                    text = "You'll receive a daily quote at ${formatTime(uiState.notificationTime)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider()

            // Auto Sync
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Auto Sync")
                    Text(
                        "Sync favorites across devices",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = uiState.autoSync,
                    onCheckedChange = { viewModel.toggleAutoSync() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "All settings are saved automatically and persist across app restarts.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        } // Close Box with gradient background

        // Theme Picker Dialog
        if (showThemePicker) {
            AlertDialog(
                onDismissRequest = { showThemePicker = false },
                title = { Text("Select Theme") },
                text = {
                    Column {
                        themes.forEach { theme ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setTheme(theme)
                                        showThemePicker = false
                                    }
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(theme)
                                RadioButton(
                                    selected = uiState.theme == theme,
                                    onClick = {
                                        viewModel.setTheme(theme)
                                        showThemePicker = false
                                    }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showThemePicker = false }) {
                        Text("Close")
                    }
                }
            )
        }

        // Time Picker Dialog
        if (showTimePicker) {
            TimePickerDialog(
                currentTime = uiState.notificationTime,
                onTimeSelected = { hour, minute ->
                    Log.d(TAG, "⏰ onTimeSelected RECEIVED: hour=$hour, minute=$minute")
                    Log.d(TAG, "⏰ Calling viewModel.setNotificationTime()")
                    viewModel.setNotificationTime(hour, minute)
                    Log.d(TAG, "⏰ viewModel.setNotificationTime() called")
                    showTimePicker = false

                    // Show confirmation toast
                    val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
                    val amPm = if (hour < 12) "AM" else "PM"
                    Toast.makeText(
                        context,
                        "Notification time set to $displayHour:${String.format("%02d", minute)} $amPm",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(TAG, "⏰ Toast shown, dialog will close")
                },
                onDismiss = {
                    Log.d(TAG, "⏰ Time picker dismissed without selection")
                    showTimePicker = false
                }
            )
        }

        // Permission Denied Dialog
        if (showPermissionDeniedDialog) {
            AlertDialog(
                onDismissRequest = { showPermissionDeniedDialog = false },
                title = { Text("Permission Required") },
                text = {
                    Text("Notification permission is required to send you daily quote reminders. Please enable it in Settings.")
                },
                confirmButton = {
                    TextButton(onClick = { showPermissionDeniedDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    currentTime: String,
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val parts = currentTime.split(":")
    val initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 9
    val initialMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false // Use 12-hour format with AM/PM
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Time",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Material 3 TimePicker with circular clock
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = MaterialTheme.colorScheme.primaryContainer,
                        selectorColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        periodSelectorBorderColor = MaterialTheme.colorScheme.outline,
                        periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val hour = timePickerState.hour
                            val minute = timePickerState.minute
                            Log.d(TAG, "🕐 TimePicker OK clicked: hour=$hour, minute=$minute")
                            Log.d(TAG, "🕐 Calling onTimeSelected callback...")
                            onTimeSelected(hour, minute)
                            Log.d(TAG, "🕐 onTimeSelected callback completed")
                        }
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

private fun formatTime(timeStr: String): String {
    return try {
        val parts = timeStr.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 9
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val amPm = if (hour < 12) "AM" else "PM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        String.format("%d:%02d %s", displayHour, minute, amPm)
    } catch (e: Exception) {
        "9:00 AM"
    }
}

