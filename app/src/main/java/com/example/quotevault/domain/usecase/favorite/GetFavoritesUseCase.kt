package com.example.quotevault.domain.usecase.favorite

import com.example.quotevault.core.util.Resource
import com.example.quotevault.domain.model.Quote
import com.example.quotevault.domain.repository.FavoriteRepository
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(): Resource<List<Quote>> {
        return favoriteRepository.getFavorites()
    }
}
