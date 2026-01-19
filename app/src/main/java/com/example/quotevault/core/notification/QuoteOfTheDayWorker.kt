package com.example.quotevault.core.notification

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.quotevault.domain.usecase.quote.GetQuoteOfTheDayUseCase
import com.example.quotevault.core.util.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

private const val TAG = "QV_QuoteWorker"

@HiltWorker
class QuoteOfTheDayWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getQuoteOfTheDayUseCase: GetQuoteOfTheDayUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "QuoteOfTheDayWorker doWork started")
        return try {
            val result = getQuoteOfTheDayUseCase()
            Log.d(TAG, "GetQuoteOfTheDayUseCase result: $result")

            when (result) {
                is Resource.Success -> {
                    val quote = result.data
                    if (quote != null) {
                        Log.d(TAG, "Showing notification for quote: ${quote.text.take(50)}...")
                        val notificationManager = QuoteNotificationManager(applicationContext)
                        notificationManager.showQuoteNotification(quote.text, quote.author)
                        Log.d(TAG, "Notification shown successfully")
                        Result.success()
                    } else {
                        Log.e(TAG, "Quote is null")
                        Result.failure()
                    }
                }
                is Resource.Error -> {
                    Log.e(TAG, "Error getting quote: ${result.message}")
                    Result.retry()
                }
                is Resource.Loading -> {
                    Log.d(TAG, "Still loading...")
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in doWork", e)
            Result.retry()
        }
    }
}

