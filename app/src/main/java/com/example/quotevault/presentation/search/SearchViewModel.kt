package com.example.quotevault.presentation.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotevault.core.util.Constants
import com.example.quotevault.core.util.Resource
import com.example.quotevault.domain.usecase.favorite.ToggleFavoriteUseCase
import com.example.quotevault.domain.usecase.quote.GetQuotesByCategoryUseCase
import com.example.quotevault.domain.usecase.quote.SearchQuotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "QV_SearchVM"

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchQuotesUseCase: SearchQuotesUseCase,
    private val getQuotesByCategoryUseCase: GetQuotesByCategoryUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    fun onEvent(event: SearchEvent) {
        Log.d(TAG, "onEvent: $event")
        when (event) {
            is SearchEvent.QueryChanged -> {
                Log.d(TAG, "QueryChanged: '${event.query}'")
                _state.update { it.copy(query = event.query) }
                if (event.query.length >= 2) {
                    searchQuotes(event.query)
                } else {
                    Log.d(TAG, "Query too short, clearing quotes")
                    _state.update { it.copy(quotes = emptyList()) }
                }
            }
            is SearchEvent.ClearQuery -> {
                Log.d(TAG, "ClearQuery")
                _state.update { it.copy(query = "", quotes = emptyList()) }
            }
            is SearchEvent.ToggleFavorite -> {
                Log.d(TAG, "ToggleFavorite: ${event.quoteId}")
                toggleFavorite(event.quoteId)
            }
            is SearchEvent.LoadAllQuotes -> {
                Log.d(TAG, "LoadAllQuotes event received")
                loadAllQuotes()
            }
            is SearchEvent.CategorySelected -> {
                Log.d(TAG, "CategorySelected: ${event.category}")
                loadQuotesByCategory(event.category)
            }
        }
    }

    private fun searchQuotes(query: String) {
        Log.d(TAG, "searchQuotes called with query: '$query'")
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = searchQuotesUseCase(query)) {
                is Resource.Success -> {
                    val quotes = result.data ?: emptyList()
                    Log.d(TAG, "Search success: found ${quotes.size} quotes")
                    _state.update {
                        it.copy(
                            quotes = quotes,
                            isLoading = false
                        )
                    }
                }
                is Resource.Error -> {
                    Log.e(TAG, "Search error: ${result.message}")
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

    private fun loadAllQuotes() {
        Log.d(TAG, "loadAllQuotes called")
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, query = "") }

            // Use GetQuotesByCategoryUseCase with "All" category (same as HomeViewModel)
            when (val result = getQuotesByCategoryUseCase(
                category = "All",
                page = 0,
                limit = Constants.PAGE_SIZE
            )) {
                is Resource.Success -> {
                    val quotes = result.data ?: emptyList()
                    Log.d(TAG, "LoadAllQuotes success: loaded ${quotes.size} quotes")
                    _state.update {
                        it.copy(
                            quotes = quotes,
                            isLoading = false
                        )
                    }
                }
                is Resource.Error -> {
                    Log.e(TAG, "LoadAllQuotes error: ${result.message}")
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

    private fun loadQuotesByCategory(category: String) {
        Log.d(TAG, "loadQuotesByCategory called with category: $category")
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, query = "") }

            when (val result = getQuotesByCategoryUseCase(
                category = category,
                page = 0,
                limit = Constants.PAGE_SIZE
            )) {
                is Resource.Success -> {
                    val quotes = result.data ?: emptyList()
                    Log.d(TAG, "LoadQuotesByCategory success: loaded ${quotes.size} quotes for $category")
                    _state.update {
                        it.copy(
                            quotes = quotes,
                            isLoading = false
                        )
                    }
                }
                is Resource.Error -> {
                    Log.e(TAG, "LoadQuotesByCategory error: ${result.message}")
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

    private fun toggleFavorite(quoteId: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase(quoteId)
        }
    }
}

sealed class SearchEvent {
    data class QueryChanged(val query: String) : SearchEvent()
    object ClearQuery : SearchEvent()
    data class ToggleFavorite(val quoteId: String) : SearchEvent()
    object LoadAllQuotes : SearchEvent()
    data class CategorySelected(val category: String) : SearchEvent()
}
