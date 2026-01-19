package com.example.quotevault.presentation.settings

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi

private const val TAG = "QV_PermissionHelper"

/**
 * Helper class for notification-related permissions and settings
 */
object NotificationPermissionHelper {

    /**
     * Check if exact alarm permission is granted (Android 12+)
     */
    fun canScheduleExactAlarms(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val hasPermission = alarmManager.canScheduleExactAlarms()
            Log.d(TAG, "📱 Exact alarm permission: ${if (hasPermission) "✅ GRANTED" else "❌ DENIED"}")
            hasPermission
        } else {
            Log.d(TAG, "📱 Android < 12, no exact alarm permission needed")
            true
        }
    }

    /**
     * Open exact alarm permission settings (Android 12+)
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun openExactAlarmSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
            Log.d(TAG, "🔓 Opened exact alarm settings")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to open exact alarm settings", e)
            // Fallback to app settings
            openAppSettings(context)
        }
    }

    /**
     * Check if battery optimization is disabled
     */
    fun isBatteryOptimizationDisabled(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val isWhitelisted = powerManager.isIgnoringBatteryOptimizations(context.packageName)
            Log.d(TAG, "🔋 Battery optimization: ${if (isWhitelisted) "✅ DISABLED" else "⚠️ ENABLED"}")
            isWhitelisted
        } else {
            true
        }
    }

    /**
     * Open battery optimization settings
     */
    fun openBatteryOptimizationSettings(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
                Log.d(TAG, "🔋 Opened battery optimization settings")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to open battery optimization settings", e)
            // Fallback to app settings
            openAppSettings(context)
        }
    }

    /**
     * Open app notification settings
     */
    fun openNotificationSettings(context: Context) {
        try {
            val intent = Intent().apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                } else {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.parse("package:${context.packageName}")
                }
            }
            context.startActivity(intent)
            Log.d(TAG, "🔔 Opened notification settings")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to open notification settings", e)
            openAppSettings(context)
        }
    }

    /**
     * Open app settings page
     */
    fun openAppSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
            Log.d(TAG, "⚙️ Opened app settings")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to open app settings", e)
        }
    }

    /**
     * Get comprehensive permission status message
     */
    fun getPermissionStatusMessage(context: Context): String {
        val messages = mutableListOf<String>()

        // Check exact alarm permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!canScheduleExactAlarms(context)) {
                messages.add("⚠️ Exact alarm permission required")
            }
        }

        // Check battery optimization
        if (!isBatteryOptimizationDisabled(context)) {
            messages.add("⚠️ Battery optimization may block notifications")
        }

        return if (messages.isEmpty()) {
            "✅ All permissions granted"
        } else {
            messages.joinToString("\n")
        }
    }

    /**
     * Check if all required permissions are granted
     */
    fun hasAllRequiredPermissions(context: Context): Boolean {
        val exactAlarmOk = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            canScheduleExactAlarms(context)
        } else {
            true
        }

        val batteryOk = isBatteryOptimizationDisabled(context)

        Log.d(TAG, "📋 Permission check: ExactAlarm=$exactAlarmOk, Battery=$batteryOk")

        return exactAlarmOk && batteryOk
    }
}
