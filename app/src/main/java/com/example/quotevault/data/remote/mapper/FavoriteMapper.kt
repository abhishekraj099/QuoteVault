package com.example.quotevault.data.remote.mapper

import com.example.quotevault.data.remote.dto.FavoriteDto
import com.example.quotevault.domain.model.Favorite
import java.text.SimpleDateFormat
import java.util.Locale

fun FavoriteDto.toFavorite(): Favorite {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val timestamp = try {
        dateFormat.parse(created_at)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }

    return Favorite(
        id = id,
        userId = user_id,
        quoteId = quote_id,
        createdAt = timestamp
    )
}
