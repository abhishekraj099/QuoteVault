package com.example.quotevault.data.remote.mapper

import com.example.quotevault.data.remote.dto.CollectionDto
import com.example.quotevault.domain.model.Collection
import java.text.SimpleDateFormat
import java.util.Locale

fun CollectionDto.toCollection(quoteCount: Int = 0): Collection {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val timestamp = try {
        dateFormat.parse(created_at)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }

    return Collection(
        id = id,
        userId = user_id,
        name = name,
        quoteCount = quoteCount,
        createdAt = timestamp
    )
}

fun List<CollectionDto>.toCollections(): List<Collection> {
    return map { it.toCollection() }
}
