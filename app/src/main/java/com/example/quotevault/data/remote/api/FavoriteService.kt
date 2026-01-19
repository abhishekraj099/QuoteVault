package com.example.quotevault.data.remote.api

import com.example.quotevault.core.util.Constants
import com.example.quotevault.data.remote.dto.QuoteDto
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class FavoriteService @Inject constructor(
    private val supabaseClient: SupabaseClientProvider
) {
    private val postgrest: Postgrest get() = supabaseClient.client.postgrest

    suspend fun addFavorite(userId: String, quoteId: String) {
        postgrest.from(Constants.TABLE_FAVORITES)
            .insert(
                mapOf(
                    "user_id" to userId,
                    "quote_id" to quoteId
                )
            )
    }

    suspend fun removeFavorite(userId: String, quoteId: String) {
        postgrest.from(Constants.TABLE_FAVORITES)
            .delete {
                filter {
                    eq("user_id", userId)
                    eq("quote_id", quoteId)
                }
            }
    }

    suspend fun getFavorites(userId: String): List<QuoteDto> {
        val favorites = postgrest.from(Constants.TABLE_FAVORITES)
            .select(columns = Columns.list("quote_id")) {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<Map<String, String>>()

        val quoteIds = favorites.mapNotNull { it["quote_id"] }

        if (quoteIds.isEmpty()) return emptyList()

        return postgrest.from(Constants.TABLE_QUOTES)
            .select(columns = Columns.ALL) {
                filter {
                    isIn("id", quoteIds)
                }
            }
            .decodeList<QuoteDto>()
    }

    suspend fun isFavorite(userId: String, quoteId: String): Boolean {
        val result = postgrest.from(Constants.TABLE_FAVORITES)
            .select(columns = Columns.list("id")) {
                filter {
                    eq("user_id", userId)
                    eq("quote_id", quoteId)
                }
                limit(1)
            }
            .decodeList<Map<String, String>>()

        return result.isNotEmpty()
    }
}
