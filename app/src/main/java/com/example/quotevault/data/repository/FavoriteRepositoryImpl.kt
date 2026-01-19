package com.example.quotevault.data.repository

import com.example.quotevault.core.util.Resource
import com.example.quotevault.data.remote.api.AuthService
import com.example.quotevault.data.remote.api.FavoriteService
import com.example.quotevault.data.remote.mapper.toQuotes
import com.example.quotevault.domain.model.Quote
import com.example.quotevault.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteService: FavoriteService,
    private val authService: AuthService
) : FavoriteRepository {

    override suspend fun toggleFavorite(quoteId: String): Resource<Unit> {
        return try {
            val userId = authService.getCurrentUser()?.id
                ?: return Resource.Error("User not logged in")

            val isFav = favoriteService.isFavorite(userId, quoteId)

            if (isFav) {
                favoriteService.removeFavorite(userId, quoteId)
            } else {
                favoriteService.addFavorite(userId, quoteId)
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to toggle favorite")
        }
    }

    override suspend fun getFavorites(): Resource<List<Quote>> {
        return try {
            val userId = authService.getCurrentUser()?.id
                ?: return Resource.Error("User not logged in")

            val quotes = favoriteService.getFavorites(userId)
            Resource.Success(quotes.toQuotes())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch favorites")
        }
    }

    override suspend fun isFavorite(quoteId: String): Boolean {
        return try {
            val userId = authService.getCurrentUser()?.id ?: return false
            favoriteService.isFavorite(userId, quoteId)
        } catch (e: Exception) {
            false
        }
    }

    override fun getFavoritesFlow(): Flow<List<Quote>> = flow {
        try {
            val userId = authService.getCurrentUser()?.id ?: return@flow
            val quotes = favoriteService.getFavorites(userId)
            emit(quotes.toQuotes())
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
