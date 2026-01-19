package com.example.quotevault.presentation.collections.list

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
import com.example.quotevault.presentation.collections.CollectionsState

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CollectionsState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadCollections()
    }

    fun onEvent(event: CollectionsEvent) {
        when (event) {
            is CollectionsEvent.ShowCreateDialog -> {
                _state.update { it.copy(showCreateDialog = true) }
            }
            is CollectionsEvent.HideCreateDialog -> {
                _state.update { it.copy(showCreateDialog = false, newCollectionName = "") }
            }
            is CollectionsEvent.CollectionNameChanged -> {
                _state.update { it.copy(newCollectionName = event.name) }
            }
            is CollectionsEvent.CreateCollection -> {
                createCollectionWithName(event.name)
            }
            is CollectionsEvent.CreateCollectionFromDialog -> {
                createCollection()
            }
            is CollectionsEvent.AddQuoteToCollection -> {
                addQuoteToCollection(event.collectionId, event.quoteId)
            }
            is CollectionsEvent.CreateCollectionAndAddQuote -> {
                createCollectionAndAddQuote(event.name, event.quoteId)
            }
            is CollectionsEvent.DeleteCollection -> {
                deleteCollection(event.collectionId)
            }
            is CollectionsEvent.Refresh -> {
                loadCollections()
            }
        }
    }

    private fun loadCollections() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = collectionRepository.getCollections()) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            collections = result.data ?: emptyList(),
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

    private fun createCollection() {
        viewModelScope.launch {
            if (state.value.newCollectionName.isBlank()) {
                _uiEvent.send(UiEvent.ShowSnackbar("Collection name cannot be empty"))
                return@launch
            }

            when (val result = collectionRepository.createCollection(state.value.newCollectionName)) {
                is Resource.Success -> {
                    _state.update { it.copy(showCreateDialog = false, newCollectionName = "") }
                    _uiEvent.send(UiEvent.ShowSnackbar("Collection created!"))
                    loadCollections()
                }
                is Resource.Error -> {
                    _uiEvent.send(UiEvent.ShowSnackbar(result.message ?: "Failed to create collection"))
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun createCollectionWithName(name: String) {
        viewModelScope.launch {
            if (name.isBlank()) {
                _uiEvent.send(UiEvent.ShowSnackbar("Collection name cannot be empty"))
                return@launch
            }

            when (val result = collectionRepository.createCollection(name)) {
                is Resource.Success -> {
                    _uiEvent.send(UiEvent.ShowSnackbar("Collection created!"))
                    loadCollections()
                }
                is Resource.Error -> {
                    _uiEvent.send(UiEvent.ShowSnackbar(result.message ?: "Failed to create collection"))
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun addQuoteToCollection(collectionId: String, quoteId: String) {
        viewModelScope.launch {
            android.util.Log.d("CollectionsVM", "═══ addQuoteToCollection CALLED ═══")
            android.util.Log.d("CollectionsVM", "collectionId: $collectionId")
            android.util.Log.d("CollectionsVM", "quoteId: $quoteId")

            when (val result = collectionRepository.addQuoteToCollection(collectionId, quoteId)) {
                is Resource.Success -> {
                    android.util.Log.d("CollectionsVM", "✅ SUCCESS - Quote added")
                    _uiEvent.send(UiEvent.ShowSnackbar("✅ Quote added to collection!"))
                    loadCollections() // Reload to update quote counts
                }
                is Resource.Error -> {
                    android.util.Log.e("CollectionsVM", "❌ ERROR: ${result.message}")
                    _uiEvent.send(UiEvent.ShowSnackbar("❌ ${result.message ?: "Failed to add quote"}"))
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun createCollectionAndAddQuote(name: String, quoteId: String) {
        viewModelScope.launch {
            if (name.isBlank()) {
                _uiEvent.send(UiEvent.ShowSnackbar("Collection name cannot be empty"))
                return@launch
            }

            // First, create the collection
            when (val createResult = collectionRepository.createCollection(name)) {
                is Resource.Success -> {
                    val newCollection = createResult.data
                    if (newCollection != null) {
                        // Then add the quote to the newly created collection
                        when (val addResult = collectionRepository.addQuoteToCollection(newCollection.id, quoteId)) {
                            is Resource.Success -> {
                                _uiEvent.send(UiEvent.ShowSnackbar("Collection created and quote added!"))
                                loadCollections() // Reload to show new collection with quote
                            }
                            is Resource.Error -> {
                                _uiEvent.send(UiEvent.ShowSnackbar("Collection created but failed to add quote: ${addResult.message}"))
                                loadCollections()
                            }
                            is Resource.Loading -> {}
                        }
                    } else {
                        _uiEvent.send(UiEvent.ShowSnackbar("Collection created but failed to add quote"))
                        loadCollections()
                    }
                }
                is Resource.Error -> {
                    _uiEvent.send(UiEvent.ShowSnackbar(createResult.message ?: "Failed to create collection"))
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun deleteCollection(collectionId: String) {
        viewModelScope.launch {
            when (val result = collectionRepository.deleteCollection(collectionId)) {
                is Resource.Success -> {
                    _uiEvent.send(UiEvent.ShowSnackbar("Collection deleted"))
                    loadCollections()
                }
                is Resource.Error -> {
                    _uiEvent.send(UiEvent.ShowSnackbar(result.message ?: "Failed to delete collection"))
                }
                is Resource.Loading -> {}
            }
        }
    }
}
