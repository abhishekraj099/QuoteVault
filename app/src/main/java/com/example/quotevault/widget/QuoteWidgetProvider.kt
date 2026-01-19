package com.example.quotevault.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.work.*
import com.example.quotevault.MainActivity
import java.util.concurrent.TimeUnit

/**
 * Quote of the Day Widget Provider
 * Displays daily quote on home screen
 */
class QuoteWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Schedule daily updates
        scheduleDailyUpdate(context)
    }

    override fun onDisabled(context: Context) {
        // Cancel scheduled updates
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    companion object {
        private const val WORK_NAME = "quote_widget_update"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // Use custom widget layout
            try {
                val views = android.widget.RemoteViews(
                    context.packageName,
                    com.example.quotevault.R.layout.widget_quote
                )

                val quote = getQuoteOfTheDay(context)

                // Set quote text and author
                views.setTextViewText(
                    com.example.quotevault.R.id.widget_quote_text,
                    "\"${quote.text}\""
                )
                views.setTextViewText(
                    com.example.quotevault.R.id.widget_quote_author,
                    "— ${quote.author}"
                )

                // Setup click to open app
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("quote_id", quote.id)
                    putExtra("open_quote_detail", true)
                }
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    appWidgetId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(
                    com.example.quotevault.R.id.widget_container,
                    pendingIntent
                )

                // Update widget
                appWidgetManager.updateAppWidget(appWidgetId, views)
                android.util.Log.d("QuoteWidget", "Widget updated successfully with ID: $appWidgetId")
            } catch (e: Exception) {
                android.util.Log.e("QuoteWidget", "Error updating widget", e)
            }
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, QuoteWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }

        @Suppress("UNUSED_PARAMETER")
        private fun getQuoteOfTheDay(context: Context): QuoteData {
            // Return a default inspirational quote
            // In production, this would fetch from Supabase
            return QuoteData(
                id = "daily",
                text = "The only way to do great work is to love what you do.",
                author = "Steve Jobs"
            )
        }

        private fun scheduleDailyUpdate(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
                24, TimeUnit.HOURS
            )
                .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        private fun calculateInitialDelay(): Long {
            val now = System.currentTimeMillis()
            val midnight = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                add(java.util.Calendar.DAY_OF_MONTH, 1)
            }.timeInMillis
            return midnight - now
        }
    }
}

data class QuoteData(
    val id: String,
    val text: String,
    val author: String
)

/**
 * Worker to update widget daily
 */
class WidgetUpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            QuoteWidgetProvider.updateAllWidgets(applicationContext)
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}

