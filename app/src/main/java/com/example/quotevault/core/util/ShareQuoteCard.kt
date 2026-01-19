package com.example.quotevault.core.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

fun saveAndShareQuoteCard(
    context: Context,
    bitmap: Bitmap,
    quote: String,
    author: String
) {
    try {
        // Save bitmap to cache
        val file = File(context.cacheDir, "quote_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
        }

        // Create share intent
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "\"$quote\" — $author\n\nShared from QuoteVault")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Quote Card"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun saveQuoteCardToGallery(
    context: Context,
    bitmap: Bitmap,
    quote: String
): Boolean {
    return try {
        val filename = "QuoteVault_${System.currentTimeMillis()}.png"
        val file = File(context.getExternalFilesDir(null), filename)

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
        }

        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

