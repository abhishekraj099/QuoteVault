package com.example.quotevault.domain.usecase.favorite

import com.example.quotevault.core.util.Resource
import com.example.quotevault.domain.repository.FavoriteRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(quoteId: String): Resource<Unit> {
        return favoriteRepository.toggleFavorite(quoteId)
    }
}
