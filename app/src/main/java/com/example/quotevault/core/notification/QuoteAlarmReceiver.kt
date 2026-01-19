package com.example.quotevault.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "QV_AlarmReceiver"

/**
 * BroadcastReceiver that triggers the daily quote notification
 * This is more reliable than WorkManager on restrictive OEM devices
 */
@AndroidEntryPoint
class QuoteAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: QuoteNotificationManager

    @Inject
    lateinit var scheduler: NotificationScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val timestamp = System.currentTimeMillis()
        val date = java.util.Date(timestamp)
        val now = java.util.Calendar.getInstance()

        // Get expected trigger time from intent
        val expectedHour = intent.getIntExtra("hour", -1)
        val expectedMinute = intent.getIntExtra("minute", -1)

        // Get actual trigger time
        val actualHour = now.get(java.util.Calendar.HOUR_OF_DAY)
        val actualMinute = now.get(java.util.Calendar.MINUTE)

        Log.d(TAG, "========================================")
        Log.d(TAG, "🔔 QuoteAlarmReceiver onReceive TRIGGERED!")
        Log.d(TAG, "⏰ Actual time:   $date")
        Log.d(TAG, "⏰ Expected time: $expectedHour:${String.format("%02d", expectedMinute)}")
        Log.d(TAG, "⏰ Actual time:   $actualHour:${String.format("%02d", actualMinute)}")

        // Calculate delay if any
        if (expectedHour >= 0 && expectedMinute >= 0) {
            val expectedTotalMinutes = expectedHour * 60 + expectedMinute
            val actualTotalMinutes = actualHour * 60 + actualMinute
            val delayMinutes = actualTotalMinutes - expectedTotalMinutes

            if (Math.abs(delayMinutes) > 5) {
                Log.w(TAG, "⚠️ TIMING DISCREPANCY: ${delayMinutes} minutes")
                if (delayMinutes > 0) {
                    Log.w(TAG, "⚠️ Notification was DELAYED by $delayMinutes minutes")
                } else {
                    Log.w(TAG, "⚠️ Notification triggered ${-delayMinutes} minutes EARLY")
                }
            } else {
                Log.d(TAG, "✅ Timing accurate (within 5 minutes)")
            }
        }

        Log.d(TAG, "📱 Action: ${intent.action}")
        Log.d(TAG, "========================================")

        // Use goAsync() to get more time for the operation
        val pendingResult = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        scope.launch {
            try {
                // Show notification with a random quote
                val quote = getRandomQuote()
                val author = getRandomAuthor()

                Log.d(TAG, "📝 Showing notification: $quote by $author")
                notificationManager.showQuoteNotification(
                    quoteText = quote,
                    author = author
                )
                Log.d(TAG, "✅ Notification shown successfully")

                // Reschedule for tomorrow
                val hour = intent.getIntExtra("hour", 9)
                val minute = intent.getIntExtra("minute", 0)
                Log.d(TAG, "🔄 Rescheduling for tomorrow at $hour:$minute")

                scheduler.scheduleQuoteNotification(hour, minute)
                Log.d(TAG, "✅ Rescheduled successfully")

            } catch (e: Exception) {
                Log.e(TAG, "❌ ERROR in QuoteAlarmReceiver", e)
                Log.e(TAG, "❌ Error message: ${e.message}")
                Log.e(TAG, "❌ Stack trace:", e)
            } finally {
                // Must call finish() when done
                pendingResult.finish()
                scope.cancel()
                Log.d(TAG, "========================================")
            }
        }
    }

    private fun getRandomQuote(): String {
        val quotes = listOf(
            "The only way to do great work is to love what you do.",
            "Believe you can and you're halfway there.",
            "The future belongs to those who believe in the beauty of their dreams.",
            "It does not matter how slowly you go as long as you do not stop.",
            "Success is not final, failure is not fatal: it is the courage to continue that counts.",
            "The only impossible journey is the one you never begin.",
            "What lies behind us and what lies before us are tiny matters compared to what lies within us.",
            "Happiness is not something ready made. It comes from your own actions.",
            "In the middle of difficulty lies opportunity.",
            "The best time to plant a tree was 20 years ago. The second best time is now."
        )
        return quotes.random()
    }

    private fun getRandomAuthor(): String {
        val authors = listOf(
            "Steve Jobs",
            "Theodore Roosevelt",
            "Eleanor Roosevelt",
            "Confucius",
            "Winston Churchill",
            "Tony Robbins",
            "Ralph Waldo Emerson",
            "Dalai Lama",
            "Albert Einstein",
            "Chinese Proverb"
        )
        return authors.random()
    }
}

