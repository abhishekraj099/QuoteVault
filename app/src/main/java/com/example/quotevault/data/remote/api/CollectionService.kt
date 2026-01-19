package com.example.quotevault.data.remote.api

import com.example.quotevault.core.util.Constants
import com.example.quotevault.data.remote.dto.CollectionDto
import com.example.quotevault.data.remote.dto.QuoteDto
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class CollectionService @Inject constructor(
    private val supabaseClient: SupabaseClientProvider
) {
    private val postgrest: Postgrest get() = supabaseClient.client.postgrest

    suspend fun createCollection(userId: String, name: String): CollectionDto {
        return postgrest.from(Constants.TABLE_COLLECTIONS)
            .insert(
                mapOf(
                    "user_id" to userId,
                    "name" to name
                )
            ) {
                select()
            }
            .decodeSingle<CollectionDto>()
    }

    suspend fun getCollections(userId: String): List<CollectionDto> {
        return postgrest.from(Constants.TABLE_COLLECTIONS)
            .select(columns = Columns.ALL) {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<CollectionDto>()
    }

    suspend fun getCollectionById(id: String): CollectionDto {
        return postgrest.from(Constants.TABLE_COLLECTIONS)
            .select(columns = Columns.ALL) {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingle<CollectionDto>()
    }

    suspend fun updateCollection(id: String, name: String) {
        postgrest.from(Constants.TABLE_COLLECTIONS)
            .update(
                mapOf("name" to name)
            ) {
                filter {
                    eq("id", id)
                }
            }
    }

    suspend fun deleteCollection(id: String) {
        postgrest.from(Constants.TABLE_COLLECTIONS)
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }

    suspend fun addQuoteToCollection(userId: String, collectionId: String, quoteId: String) {
        try {
            android.util.Log.d("CollectionService", "═══ addQuoteToCollection START ═══")
            android.util.Log.d("CollectionService", "userId: $userId")
            android.util.Log.d("CollectionService", "collectionId: $collectionId")
            android.util.Log.d("CollectionService", "quoteId: $quoteId")

            postgrest.from(Constants.TABLE_COLLECTION_QUOTES)
                .insert(
                    mapOf(
                        "user_id" to userId,        // ✅ FIX: Include user_id for RLS
                        "collection_id" to collectionId,
                        "quote_id" to quoteId
                    )
                )

            android.util.Log.d("CollectionService", "✅ Insert successful")
        } catch (e: Exception) {
            android.util.Log.e("CollectionService", "❌ Insert failed: ${e.message}", e)
            throw e // Re-throw so repository can handle
        }
    }

    suspend fun removeQuoteFromCollection(userId: String, collectionId: String, quoteId: String) {
        try {
            android.util.Log.d("CollectionService", "═══ removeQuoteFromCollection START ═══")
            android.util.Log.d("CollectionService", "userId: $userId, collectionId: $collectionId, quoteId: $quoteId")

            postgrest.from(Constants.TABLE_COLLECTION_QUOTES)
                .delete {
                    filter {
                        eq("user_id", userId)       // ✅ FIX: Add user_id filter
                        eq("collection_id", collectionId)
                        eq("quote_id", quoteId)
                    }
                }

            android.util.Log.d("CollectionService", "✅ Delete successful")
        } catch (e: Exception) {
            android.util.Log.e("CollectionService", "❌ Delete failed: ${e.message}", e)
            throw e
        }
    }

    suspend fun getCollectionQuotes(userId: String, collectionId: String): List<QuoteDto> {
        try {
            android.util.Log.d("CollectionService", "═══ getCollectionQuotes START ═══")
            android.util.Log.d("CollectionService", "userId: $userId, collectionId: $collectionId")

            val items = postgrest.from(Constants.TABLE_COLLECTION_QUOTES)
                .select(columns = Columns.list("quote_id")) {
                    filter {
                        eq("user_id", userId)       // ✅ FIX: Add user_id filter
                        eq("collection_id", collectionId)
                    }
                }
                .decodeList<Map<String, String>>()

            android.util.Log.d("CollectionService", "Found ${items.size} items in collection")

            val quoteIds = items.mapNotNull { it["quote_id"] }

            if (quoteIds.isEmpty()) {
                android.util.Log.d("CollectionService", "No quotes in collection")
                return emptyList()
            }

            val quotes = postgrest.from(Constants.TABLE_QUOTES)
                .select(columns = Columns.ALL) {
                    filter {
                        isIn("id", quoteIds)
                    }
                }
                .decodeList<QuoteDto>()

            android.util.Log.d("CollectionService", "✅ Fetched ${quotes.size} quotes")
            return quotes
        } catch (e: Exception) {
            android.util.Log.e("CollectionService", "❌ Fetch failed: ${e.message}", e)
            throw e
        }
    }

    suspend fun getCollectionQuoteCount(userId: String, collectionId: String): Int {
        try {
            val items = postgrest.from(Constants.TABLE_COLLECTION_QUOTES)
                .select(columns = Columns.list("id")) {
                    filter {
                        eq("user_id", userId)       // ✅ FIX: Add user_id filter
                        eq("collection_id", collectionId)
                    }
                }
                .decodeList<Map<String, String>>()

            val count = items.size
            android.util.Log.d("CollectionService", "Collection $collectionId has $count quotes")
            return count
        } catch (e: Exception) {
            android.util.Log.e("CollectionService", "❌ Count failed: ${e.message}", e)
            return 0 // Fail gracefully
        }
    }
}
