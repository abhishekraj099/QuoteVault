package com.example.quotevault.presentation.favorites

import com.example.quotevault.domain.model.Quote

data class FavoritesState(
    val favorites: List<Quote> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
