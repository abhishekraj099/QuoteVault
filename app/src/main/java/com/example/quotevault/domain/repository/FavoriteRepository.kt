package com.example.quotevault.domain.repository

import com.example.quotevault.core.util.Resource
import com.example.quotevault.domain.model.Quote
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun toggleFavorite(quoteId: String): Resource<Unit>
    suspend fun getFavorites(): Resource<List<Quote>>
    suspend fun isFavorite(quoteId: String): Boolean
    fun getFavoritesFlow(): Flow<List<Quote>>
}
