package com.example.quotevault.data.remote.api

import com.example.quotevault.core.util.Constants
import com.example.quotevault.data.remote.dto.QuoteDto
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import javax.inject.Inject

class QuoteService @Inject constructor(
    private val supabaseClient: SupabaseClientProvider
) {
    private val postgrest: Postgrest get() = supabaseClient.client.postgrest

    suspend fun getQuotes(page: Int, limit: Int): List<QuoteDto> {
        val offset = page * limit
        return postgrest.from(Constants.TABLE_QUOTES)
            .select(columns = Columns.ALL) {
                range(offset.toLong(), (offset + limit - 1).toLong())
            }
            .decodeList<QuoteDto>()
    }

    suspend fun getQuotesByCategory(category: String, page: Int, limit: Int): List<QuoteDto> {
        val offset = page * limit
        return postgrest.from(Constants.TABLE_QUOTES)
            .select(columns = Columns.ALL) {
                filter {
                    eq("category", category)
                }
                range(offset.toLong(), (offset + limit - 1).toLong())
            }
            .decodeList<QuoteDto>()
    }

    suspend fun searchQuotes(query: String): List<QuoteDto> {
        // Search in text field
        val allQuotes = postgrest.from(Constants.TABLE_QUOTES)
            .select(columns = Columns.ALL)
            .decodeList<QuoteDto>()

        // Filter locally (case-insensitive)
        return allQuotes.filter { quote ->
            quote.text.contains(query, ignoreCase = true) ||
                    quote.author.contains(query, ignoreCase = true)
        }
    }

    suspend fun getQuoteById(id: String): QuoteDto {
        return postgrest.from(Constants.TABLE_QUOTES)
            .select(columns = Columns.ALL) {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingle<QuoteDto>()
    }

    suspend fun getRandomQuote(): QuoteDto {
        val quotes = postgrest.from(Constants.TABLE_QUOTES)
            .select(columns = Columns.ALL) {
                limit(100)
            }
            .decodeList<QuoteDto>()
        return quotes.random()
    }
}
