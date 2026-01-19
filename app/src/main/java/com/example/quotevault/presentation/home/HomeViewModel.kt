package com.example.quotevault.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotevault.core.util.Constants
import com.example.quotevault.core.util.Resource
import com.example.quotevault.domain.usecase.favorite.ToggleFavoriteUseCase
import com.example.quotevault.domain.usecase.quote.GetQuoteOfTheDayUseCase
import com.example.quotevault.domain.usecase.quote.GetQuotesByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getQuotesByCategoryUseCase: GetQuotesByCategoryUseCase,
    private val getQuoteOfTheDayUseCase: GetQuoteOfTheDayUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        loadQuoteOfTheDay()
        loadQuotes()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.CategorySelected -> {
                _state.update {
                    it.copy(
                        selectedCategory = event.category,
                        quotes = emptyList(),
                        currentPage = 0,
                        hasMorePages = true
                    )
                }
                loadQuotes()
            }
            is HomeEvent.ToggleFavorite -> {
                toggleFavorite(event.quoteId)
            }
            is HomeEvent.Refresh -> {
                refresh()
            }
            is HomeEvent.LoadMore -> {
                loadMoreQuotes()
            }
        }
    }

    private fun loadQuoteOfTheDay() {
        viewModelScope.launch {
            when (val result = getQuoteOfTheDayUseCase()) {
                is Resource.Success -> {
                    _state.update { it.copy(quoteOfTheDay = result.data) }
                }
                else -> {}
            }
        }
    }

    private fun loadQuotes() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = getQuotesByCategoryUseCase(
                category = state.value.selectedCategory,
                page = 0,
                limit = Constants.PAGE_SIZE
            )) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            quotes = result.data ?: emptyList(),
                            isLoading = false,
                            currentPage = 0,
                            hasMorePages = (result.data?.size ?: 0) >= Constants.PAGE_SIZE
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
                is Resource.Loading -> {}
            }
        }
    }

    private fun loadMoreQuotes() {
        if (!state.value.hasMorePages || state.value.isLoading) return

        viewModelScope.launch {
            val nextPage = state.value.currentPage + 1

            when (val result = getQuotesByCategoryUseCase(
                category = state.value.selectedCategory,
                page = nextPage,
                limit = Constants.PAGE_SIZE
            )) {
                is Resource.Success -> {
                    val newQuotes = result.data ?: emptyList()
                    _state.update {
                        it.copy(
                            quotes = it.quotes + newQuotes,
                            currentPage = nextPage,
                            hasMorePages = newQuotes.size >= Constants.PAGE_SIZE
                        )
                    }
                }
                else -> {}
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            loadQuoteOfTheDay()

            when (val result = getQuotesByCategoryUseCase(
                category = state.value.selectedCategory,
                page = 0,
                limit = Constants.PAGE_SIZE
            )) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            quotes = result.data ?: emptyList(),
                            isRefreshing = false,
                            currentPage = 0,
                            hasMorePages = (result.data?.size ?: 0) >= Constants.PAGE_SIZE
                        )
                    }
                }
                else -> {
                    _state.update { it.copy(isRefreshing = false) }
                }
            }
        }
    }

    private fun toggleFavorite(quoteId: String) {
        // Optimistic update
        _state.update { currentState ->
            val updatedQuotes = currentState.quotes.map { quote ->
                if (quote.id == quoteId) {
                    quote.copy(isFavorite = !quote.isFavorite)
                } else quote
            }
            val updatedQotd = if (currentState.quoteOfTheDay?.id == quoteId) {
                currentState.quoteOfTheDay.copy(isFavorite = !currentState.quoteOfTheDay.isFavorite)
            } else currentState.quoteOfTheDay

            currentState.copy(
                quotes = updatedQuotes,
                quoteOfTheDay = updatedQotd
            )
        }

        viewModelScope.launch {
            val result = toggleFavoriteUseCase(quoteId)
            if (result is Resource.Error) {
                // Revert update on error
                _state.update { currentState ->
                    val updatedQuotes = currentState.quotes.map { quote ->
                        if (quote.id == quoteId) {
                            quote.copy(isFavorite = !quote.isFavorite)
                        } else quote
                    }
                    val updatedQotd = if (currentState.quoteOfTheDay?.id == quoteId) {
                        currentState.quoteOfTheDay.copy(isFavorite = !currentState.quoteOfTheDay.isFavorite)
                    } else currentState.quoteOfTheDay

                    currentState.copy(
                        quotes = updatedQuotes,
                        quoteOfTheDay = updatedQotd
                    )
                }
            }
        }
    }
}

sealed class HomeEvent {
    data class CategorySelected(val category: String) : HomeEvent()
    data class ToggleFavorite(val quoteId: String) : HomeEvent()
    object Refresh : HomeEvent()
    object LoadMore : HomeEvent()
}
