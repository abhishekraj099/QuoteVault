package com.example.quotevault.presentation.collections.detail

import com.example.quotevault.domain.model.Collection
import com.example.quotevault.domain.model.Quote

data class CollectionDetailState(
    val collection: Collection? = null,
    val quotes: List<Quote> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
