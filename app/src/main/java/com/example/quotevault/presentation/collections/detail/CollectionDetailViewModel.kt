package com.example.quotevault.presentation.collections.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotevault.core.util.Resource
import com.example.quotevault.core.util.UiEvent
import com.example.quotevault.domain.repository.CollectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionDetailViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(CollectionDetailState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val collectionId: String = checkNotNull(savedStateHandle["collectionId"])

    init {
        loadCollectionDetail()
    }

    fun onEvent(event: CollectionDetailEvent) {
        when (event) {
            is CollectionDetailEvent.RemoveQuote -> {
                removeQuote(event.quoteId)
            }
            is CollectionDetailEvent.Refresh -> {
                loadCollectionDetail()
            }
        }
    }

    private fun loadCollectionDetail() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Load collection info
            when (val collectionResult = collectionRepository.getCollectionById(collectionId)) {
                is Resource.Success -> {
                    _state.update { it.copy(collection = collectionResult.data) }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = collectionResult.message
                        )
                    }
                    return@launch
                }
                is Resource.Loading -> {}
            }

            // Load quotes in collection
            when (val quotesResult = collectionRepository.getCollectionQuotes(collectionId)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            quotes = quotesResult.data ?: emptyList(),
                            isLoading = false
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = quotesResult.message
                        )
                    }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun removeQuote(quoteId: String) {
        viewModelScope.launch {
            when (val result = collectionRepository.removeQuoteFromCollection(collectionId, quoteId)) {
                is Resource.Success -> {
                    _uiEvent.send(UiEvent.ShowSnackbar("Quote removed from collection"))
                    loadCollectionDetail() // Refresh the list
                }
                is Resource.Error -> {
                    _uiEvent.send(UiEvent.ShowSnackbar(result.message ?: "Failed to remove quote"))
                }
                is Resource.Loading -> {}
            }
        }
    }
}
