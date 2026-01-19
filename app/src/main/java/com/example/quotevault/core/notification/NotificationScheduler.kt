package com.example.quotevault.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "QV_NotifScheduler"
private const val ALARM_REQUEST_CODE = 1001
private const val WORK_TAG = "daily_quote_notification"
private const val MIN_DELAY_MS = 5000L // 5 seconds minimum delay (reduced from 15s for better UX)

@Singleton
class NotificationScheduler @Inject constructor(
    private val context: Context
) {

    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private val workManager: WorkManager by lazy {
        WorkManager.getInstance(context)
    }

    fun scheduleQuoteNotification(hourOfDay: Int = 9, minuteOfDay: Int = 0) {
        Log.d(TAG, "═══════════════════════════════════════")
        Log.d(TAG, "📅 scheduleQuoteNotification() CALLED")
        Log.d(TAG, "⏰ Requested time: $hourOfDay:${String.format("%02d", minuteOfDay)}")

        // Validate input
        if (hourOfDay < 0 || hourOfDay > 23) {
            Log.e(TAG, "❌ INVALID HOUR: $hourOfDay (must be 0-23)")
            Log.e(TAG, "═══════════════════════════════════════")
            return
        }
        if (minuteOfDay < 0 || minuteOfDay > 59) {
            Log.e(TAG, "❌ INVALID MINUTE: $minuteOfDay (must be 0-59)")
            Log.e(TAG, "═══════════════════════════════════════")
            return
        }

        Log.d(TAG, "📱 Device: ${Build.MANUFACTURER} ${Build.MODEL}")
        Log.d(TAG, "🤖 Android: ${Build.VERSION.SDK_INT}")
        Log.d(TAG, "═══════════════════════════════════════")

        val now = Calendar.getInstance()
        Log.d(TAG, "🕐 Current time: ${now.time}")

        val scheduledTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minuteOfDay)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Calculate delay
        var delayMs = scheduledTime.timeInMillis - now.timeInMillis

        // If time already passed OR less than 5 seconds away, schedule for tomorrow
        if (delayMs < MIN_DELAY_MS) {
            scheduledTime.add(Calendar.DAY_OF_MONTH, 1)
            delayMs = scheduledTime.timeInMillis - now.timeInMillis

            val originalDelaySeconds = (scheduledTime.timeInMillis - now.timeInMillis - 86400000) / 1000
            Log.d(TAG, "⏭️ Time too soon or passed (was ${originalDelaySeconds}s away)")
            Log.d(TAG, "⏭️ Scheduling for TOMORROW instead")
        } else {
            Log.d(TAG, "✅ Time is in the future with sufficient buffer")
        }

        val delayMinutes = delayMs / 1000 / 60
        val delaySeconds = (delayMs / 1000) % 60
        Log.d(TAG, "⏰ Final trigger time: ${scheduledTime.time}")
        Log.d(TAG, "⏱️ Delay: ${delayMinutes}m ${delaySeconds}s (${delayMs}ms)")
        Log.d(TAG, "🔋 Buffer applied: ${MIN_DELAY_MS / 1000} seconds minimum (prevents immediate triggers)")

        // Check permissions BEFORE scheduling
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val canSchedule = alarmManager.canScheduleExactAlarms()
            Log.d(TAG, "🔐 Can schedule exact alarms: $canSchedule")
            if (!canSchedule) {
                Log.e(TAG, "❌ NO EXACT ALARM PERMISSION!")
                Log.e(TAG, "❌ User must enable: Settings → Apps → QuoteVault → Alarms")
            }
        }

        // For Oppo devices, ONLY use AlarmManager (WorkManager is unreliable)
        if (isOppoDevice()) {
            Log.d(TAG, "📱 Oppo/Realme/OnePlus device - using AlarmManager ONLY")
            scheduleWithAlarmManager(scheduledTime.timeInMillis, hourOfDay, minuteOfDay)
        } else {
            // For other devices, use both for redundancy
            Log.d(TAG, "📱 Standard device - using both AlarmManager + WorkManager")
            scheduleWithAlarmManager(scheduledTime.timeInMillis, hourOfDay, minuteOfDay)
            scheduleWithWorkManager(delayMs, hourOfDay, minuteOfDay)
        }

        Log.d(TAG, "═══════════════════════════════════════")
        Log.d(TAG, "✅ SCHEDULING COMPLETE")
        Log.d(TAG, "═══════════════════════════════════════")
    }

    private fun isOppoDevice(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return manufacturer.contains("oppo") ||
               manufacturer.contains("realme") ||
               manufacturer.contains("oneplus")
    }

    private fun scheduleWithWorkManager(delayMs: Long, hour: Int, minute: Int) {
        try {
            val data = Data.Builder()
                .putInt("hour", hour)
                .putInt("minute", minute)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<DailyQuoteWorker>()
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag(WORK_TAG)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(false)
                        .setRequiresDeviceIdle(false)
                        .build()
                )
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    10000L, // 10 seconds
                    TimeUnit.MILLISECONDS
                )
                .build()

            workManager.enqueueUniqueWork(
                WORK_TAG,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )

            Log.d(TAG, "💼 ✅ WorkManager scheduled (delay: ${delayMs}ms)")
            Log.d(TAG, "💼 Will survive app kill and device restart")
        } catch (e: Exception) {
            Log.e(TAG, "❌ WorkManager scheduling failed", e)
        }
    }

    private fun scheduleWithAlarmManager(triggerTime: Long, hour: Int, minute: Int) {
        Log.d(TAG, "--- scheduleWithAlarmManager START ---")

        val intent = Intent(context, QuoteAlarmReceiver::class.java).apply {
            action = "com.example.quotevault.DAILY_QUOTE_ALARM"
            putExtra("hour", hour)
            putExtra("minute", minute)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            // Cancel any existing alarm first
            alarmManager.cancel(pendingIntent)
            Log.d(TAG, "🗑️ Cancelled existing alarms")

            // Ensure trigger time is in the future with buffer
            val now = System.currentTimeMillis()
            val finalTriggerTime = maxOf(triggerTime, now + MIN_DELAY_MS)

            Log.d(TAG, "⏰ Trigger time: ${java.util.Date(finalTriggerTime)}")
            Log.d(TAG, "⏱️ Delay from now: ${(finalTriggerTime - now) / 1000}s")

            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    if (alarmManager.canScheduleExactAlarms()) {
                        // Use setAlarmClock for HIGHEST priority (bypasses Doze)
                        val alarmClockInfo = AlarmManager.AlarmClockInfo(
                            finalTriggerTime,
                            pendingIntent
                        )
                        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
                        Log.d(TAG, "⏰ ✅ Scheduled with setAlarmClock() [Android 12+]")
                        Log.d(TAG, "🔋 This BYPASSES Doze mode!")
                    } else {
                        // No permission - use inexact
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            finalTriggerTime,
                            pendingIntent
                        )
                        Log.w(TAG, "⚠️ Using inexact alarm (no permission)")
                        Log.w(TAG, "⚠️ Enable: Settings → Apps → QuoteVault → Alarms")
                    }
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        finalTriggerTime,
                        pendingIntent
                    )
                    Log.d(TAG, "⏰ ✅ Scheduled with setExactAndAllowWhileIdle() [Android 6-11]")
                }
                else -> {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        finalTriggerTime,
                        pendingIntent
                    )
                    Log.d(TAG, "⏰ ✅ Scheduled with setExact() [Legacy]")
                }
            }

            Log.d(TAG, "--- scheduleWithAlarmManager END (SUCCESS) ---")
        } catch (e: SecurityException) {
            Log.e(TAG, "❌ SecurityException - Missing alarm permission!", e)
            Log.e(TAG, "❌ Go to: Settings → Apps → QuoteVault → Alarms → Allow")

            // Last resort fallback
            try {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                Log.d(TAG, "⚠️ Fallback: basic alarm (may be delayed)")
            } catch (e2: Exception) {
                Log.e(TAG, "❌ All alarm methods failed!", e2)
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ AlarmManager error", e)
        }
    }

    fun cancelQuoteNotification() {
        Log.d(TAG, "═══════════════════════════════════════")
        Log.d(TAG, "🗑️ CANCELLING NOTIFICATIONS")
        Log.d(TAG, "═══════════════════════════════════════")

        // Cancel WorkManager
        try {
            workManager.cancelAllWorkByTag(WORK_TAG)
            workManager.cancelUniqueWork(WORK_TAG)
            Log.d(TAG, "✅ WorkManager cancelled")
        } catch (e: Exception) {
            Log.e(TAG, "❌ WorkManager cancel failed", e)
        }

        // Cancel AlarmManager
        val intent = Intent(context, QuoteAlarmReceiver::class.java).apply {
            action = "com.example.quotevault.DAILY_QUOTE_ALARM"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "✅ AlarmManager cancelled")
        } catch (e: Exception) {
            Log.e(TAG, "❌ AlarmManager cancel failed", e)
        }

        Log.d(TAG, "═══════════════════════════════════════")
    }

    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val canSchedule = alarmManager.canScheduleExactAlarms()
            Log.d(TAG, "🔐 Can schedule exact alarms: $canSchedule")
            canSchedule
        } else {
            Log.d(TAG, "🔐 Pre-Android 12: exact alarms allowed by default")
            true
        }
    }
}


