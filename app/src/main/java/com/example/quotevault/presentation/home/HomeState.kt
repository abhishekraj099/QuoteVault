package com.example.quotevault.presentation.home

import com.example.quotevault.domain.model.Quote

data class HomeState(
    val quotes: List<Quote> = emptyList(),
    val quoteOfTheDay: Quote? = null,
    val selectedCategory: String = "All",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val hasMorePages: Boolean = true
)
