package com.example.quotevault.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.quotevault.MainActivity
import com.example.quotevault.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "QV_NotifManager"

@Singleton
class QuoteNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val CHANNEL_ID = "quotes_channel"
        const val NOTIFICATION_ID = 1001
    }

    init {
        Log.d(TAG, "QuoteNotificationManager initialized")
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "Creating notification channel")
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Quote of the Day",
                NotificationManager.IMPORTANCE_HIGH  // Changed to HIGH for better visibility
            ).apply {
                description = "Daily quote notifications"
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created successfully")
        }
    }

    fun showQuoteNotification(quoteText: String, author: String) {
        Log.d(TAG, "showQuoteNotification called - quote: ${quoteText.take(30)}..., author: $author")

        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("navigate_to", "home")
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("✨ Quote of the Day")
                .setContentText("\"$quoteText\"")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("\"$quoteText\"\n\n— $author")
                )
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setPriority(NotificationCompat.PRIORITY_HIGH)  // HIGH priority
                .setDefaults(NotificationCompat.DEFAULT_ALL)  // Sound, vibration, lights
                .build()

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
            Log.d(TAG, "Notification sent successfully with ID: $NOTIFICATION_ID")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing notification", e)
        }
    }
}

