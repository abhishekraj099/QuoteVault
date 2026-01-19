package com.example.quotevault.domain.repository

import com.example.quotevault.core.util.Resource
import com.example.quotevault.domain.model.Quote
import kotlinx.coroutines.flow.Flow

interface QuoteRepository {
    suspend fun getQuotes(page: Int, limit: Int): Resource<List<Quote>>
    suspend fun getQuotesByCategory(category: String, page: Int, limit: Int): Resource<List<Quote>>
    suspend fun searchQuotes(query: String): Resource<List<Quote>>
    suspend fun getQuoteById(id: String): Resource<Quote>
    suspend fun getQuoteOfTheDay(): Resource<Quote>
}
