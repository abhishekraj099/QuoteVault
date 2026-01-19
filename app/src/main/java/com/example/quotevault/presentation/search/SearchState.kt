package com.example.quotevault.presentation.search

import com.example.quotevault.domain.model.Quote

data class SearchState(
    val query: String = "",
    val quotes: List<Quote> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
