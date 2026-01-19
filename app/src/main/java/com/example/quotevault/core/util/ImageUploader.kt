package com.example.quotevault.core.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import java.io.File
import java.io.InputStream

class ImageUploader {

    companion object {
        fun getImageUriFromGallery(context: Context, uri: Uri?): Uri? {
            return uri
        }

        fun compressImage(context: Context, sourceUri: Uri): ByteArray? {
            return try {
                context.contentResolver.openInputStream(sourceUri)?.use { input ->
                    input.readBytes()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

@Composable
fun rememberImagePickerLauncher(
    onImageSelected: (Uri?) -> Unit
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent(),
    onResult = { uri ->
        onImageSelected(uri)
    }
)

