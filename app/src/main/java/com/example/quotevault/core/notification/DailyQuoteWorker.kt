package com.example.quotevault.core.notification

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

private const val TAG = "QV_DailyQuoteWorker"

@HiltWorker
class DailyQuoteWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "=== WORKER TRIGGERED ===")

        return try {
            // Show the notification
            val notificationManager = QuoteNotificationManager(context)
            val quote = getRandomQuote()
            val author = getRandomAuthor()

            Log.d(TAG, "Showing notification: $quote by $author")
            notificationManager.showQuoteNotification(quote, author)

            // Reschedule for tomorrow
            val hour = inputData.getInt("hour", 9)
            val minute = inputData.getInt("minute", 0)
            Log.d(TAG, "Rescheduling for tomorrow at $hour:$minute")

            val scheduler = NotificationScheduler(context)
            scheduler.scheduleQuoteNotification(hour, minute)

            Log.d(TAG, "✅ Work completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "❌ Worker failed", e)
            Result.failure()
        }
    }

    private fun getRandomQuote(): String {
        val quotes = listOf(
            "The only way to do great work is to love what you do.",
            "Believe you can and you're halfway there.",
            "Success is not final, failure is not fatal.",
            "Dream big and dare to fail.",
            "The future belongs to those who believe in the beauty of their dreams.",
            "Do what you can, with what you have, where you are.",
            "Everything you've ever wanted is on the other side of fear.",
            "Believe in yourself. You are braver than you think.",
            "The only impossible journey is the one you never begin.",
            "Your limitation—it's only your imagination."
        )
        return quotes.random()
    }

    private fun getRandomAuthor(): String {
        val authors = listOf(
            "Steve Jobs",
            "Theodore Roosevelt",
            "Winston Churchill",
            "Norman Vaughan",
            "Eleanor Roosevelt",
            "Theodore Roosevelt",
            "George Addair",
            "Unknown",
            "Tony Robbins",
            "Unknown"
        )
        return authors.random()
    }
}
