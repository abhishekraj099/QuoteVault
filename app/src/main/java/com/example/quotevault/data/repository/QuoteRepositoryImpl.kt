package com.example.quotevault.data.repository

import com.example.quotevault.core.util.Resource
import com.example.quotevault.data.remote.api.QuoteService
import com.example.quotevault.data.remote.mapper.toQuote
import com.example.quotevault.data.remote.mapper.toQuotes
import com.example.quotevault.domain.model.Quote
import com.example.quotevault.domain.repository.QuoteRepository
import javax.inject.Inject

class QuoteRepositoryImpl @Inject constructor(
    private val quoteService: QuoteService
) : QuoteRepository {

    override suspend fun getQuotes(page: Int, limit: Int): Resource<List<Quote>> {
        return try {
            val quotes = quoteService.getQuotes(page, limit)
            Resource.Success(quotes.toQuotes())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch quotes")
        }
    }

    override suspend fun getQuotesByCategory(
        category: String,
        page: Int,
        limit: Int
    ): Resource<List<Quote>> {
        return try {
            val quotes = if (category.equals("All", ignoreCase = true)) {
                quoteService.getQuotes(page, limit)
            } else {
                quoteService.getQuotesByCategory(category, page, limit)
            }
            Resource.Success(quotes.toQuotes())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch quotes by category")
        }
    }

    override suspend fun searchQuotes(query: String): Resource<List<Quote>> {
        return try {
            val quotes = quoteService.searchQuotes(query)
            Resource.Success(quotes.toQuotes())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to search quotes")
        }
    }

    override suspend fun getQuoteById(id: String): Resource<Quote> {
        return try {
            val quote = quoteService.getQuoteById(id)
            Resource.Success(quote.toQuote())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch quote")
        }
    }

    override suspend fun getQuoteOfTheDay(): Resource<Quote> {
        return try {
            val quote = quoteService.getRandomQuote()
            Resource.Success(quote.toQuote())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch quote of the day")
        }
    }
}
