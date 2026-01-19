package com.example.quotevault.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "QV_BootReceiver"

// Use the SAME DataStore name as UserPreferencesManager
private val Context.bootDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Reschedules the notification alarm after device boot
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {

            Log.d(TAG, "Boot completed, checking if notifications need to be rescheduled")

            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val prefs = context.bootDataStore.data.first()

                    val isEnabled = prefs[booleanPreferencesKey("notifications_enabled")] ?: false
                    val timeStr = prefs[stringPreferencesKey("notification_time")] ?: "09:00"

                    if (isEnabled) {
                        val parts = timeStr.split(":")
                        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 9
                        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

                        Log.d(TAG, "Rescheduling notification for $hour:$minute")
                        val scheduler = NotificationScheduler(context)
                        scheduler.scheduleQuoteNotification(hour, minute)
                        Log.d(TAG, "Notification rescheduled after boot")
                    } else {
                        Log.d(TAG, "Notifications not enabled, skipping reschedule")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error rescheduling notification after boot", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}


