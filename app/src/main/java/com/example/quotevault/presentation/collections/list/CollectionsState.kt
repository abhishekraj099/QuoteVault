package com.example.quotevault.presentation.collections

import com.example.quotevault.domain.model.Collection

data class CollectionsState(
    val collections: List<Collection> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCreateDialog: Boolean = false,
    val newCollectionName: String = ""
)
