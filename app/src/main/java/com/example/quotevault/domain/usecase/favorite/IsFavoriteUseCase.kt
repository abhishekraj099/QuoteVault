package com.example.quotevault.domain.usecase.favorite

import com.example.quotevault.domain.repository.FavoriteRepository
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(quoteId: String): Boolean {
        return favoriteRepository.isFavorite(quoteId)
    }
}
