package com.example.quotevault.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotevault.core.util.Resource
import com.example.quotevault.domain.usecase.favorite.GetFavoritesUseCase
import com.example.quotevault.domain.usecase.favorite.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesState())
    val state = _state.asStateFlow()

    init {
        loadFavorites()
    }

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.RemoveFavorite -> {
                removeFavorite(event.quoteId)
            }
            is FavoritesEvent.Refresh -> {
                loadFavorites()
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = getFavoritesUseCase()) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            favorites = result.data ?: emptyList(),
                            isLoading = false
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun removeFavorite(quoteId: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase(quoteId)
            loadFavorites() // Reload list
        }
    }
}

sealed class FavoritesEvent {
    data class RemoveFavorite(val quoteId: String) : FavoritesEvent()
    object Refresh : FavoritesEvent()
}
