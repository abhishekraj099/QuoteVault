package com.example.quotevault.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar

private const val TAG = "QV_AlarmTest"

/**
 * Simple test receiver to verify AlarmManager is working
 * This bypasses Hilt to eliminate any DI issues
 */
class TestAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "========================================")
        Log.d(TAG, "🎯 TEST ALARM TRIGGERED!")
        Log.d(TAG, "⏰ Time: ${java.util.Date()}")
        Log.d(TAG, "📱 Action: ${intent.action}")
        Log.d(TAG, "========================================")

        // Show notification using the manager
        try {
            val notificationManager = QuoteNotificationManager(context)
            notificationManager.showQuoteNotification(
                quoteText = "Test Alarm Worked!",
                author = "AlarmManager Test"
            )
            Log.d(TAG, "✅ Test notification shown")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error showing notification", e)
        }
    }

    companion object {
        /**
         * Schedule a test alarm 30 seconds from now
         */
        fun scheduleTestAlarm(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, TestAlarmReceiver::class.java).apply {
                action = "com.example.quotevault.TEST_ALARM"
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                9999,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val triggerTime = System.currentTimeMillis() + 30000 // 30 seconds

            try {
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                        if (alarmManager.canScheduleExactAlarms()) {
                            val alarmClockInfo = AlarmManager.AlarmClockInfo(
                                triggerTime,
                                pendingIntent
                            )
                            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
                            Log.d(TAG, "✅ Test alarm scheduled for 30 seconds (setAlarmClock)")
                        } else {
                            alarmManager.setAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                triggerTime,
                                pendingIntent
                            )
                            Log.d(TAG, "⚠️ Test alarm scheduled (inexact)")
                        }
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerTime,
                            pendingIntent
                        )
                        Log.d(TAG, "✅ Test alarm scheduled for 30 seconds")
                    }
                    else -> {
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            triggerTime,
                            pendingIntent
                        )
                        Log.d(TAG, "✅ Test alarm scheduled for 30 seconds (legacy)")
                    }
                }

                Log.d(TAG, "⏰ Trigger time: ${java.util.Date(triggerTime)}")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to schedule test alarm", e)
            }
        }
    }
}
