package com.example.quotevault.data.repository

import com.example.quotevault.core.util.Resource
import com.example.quotevault.data.remote.api.AuthService
import com.example.quotevault.data.remote.api.CollectionService
import com.example.quotevault.data.remote.mapper.toCollection
import com.example.quotevault.data.remote.mapper.toCollections
import com.example.quotevault.data.remote.mapper.toQuotes
import com.example.quotevault.domain.model.Collection
import com.example.quotevault.domain.model.Quote
import com.example.quotevault.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CollectionRepositoryImpl @Inject constructor(
    private val collectionService: CollectionService,
    private val authService: AuthService
) : CollectionRepository {

    override suspend fun createCollection(name: String): Resource<Collection> {
        return try {
            val userId = authService.getCurrentUser()?.id
                ?: return Resource.Error("User not logged in")

            val collectionDto = collectionService.createCollection(userId, name)
            Resource.Success(collectionDto.toCollection())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create collection")
        }
    }

    override suspend fun getCollections(): Resource<List<Collection>> {
        return try {
            val userId = authService.getCurrentUser()?.id
                ?: return Resource.Error("User not logged in")

            android.util.Log.d("CollectionRepo", "═══ getCollections START (userId=$userId) ═══")

            val collections = collectionService.getCollections(userId)
            val collectionsWithCount = collections.map { collectionDto ->
                val count = collectionService.getCollectionQuoteCount(userId, collectionDto.id) // ✅ FIX: Pass userId
                android.util.Log.d("CollectionRepo", "Collection ${collectionDto.name} has $count quotes")
                collectionDto.toCollection(count)
            }

            android.util.Log.d("CollectionRepo", "✅ Fetched ${collectionsWithCount.size} collections")
            Resource.Success(collectionsWithCount)
        } catch (e: Exception) {
            android.util.Log.e("CollectionRepo", "❌ Failed: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to fetch collections")
        }
    }

    override suspend fun getCollectionById(id: String): Resource<Collection> {
        return try {
            val userId = authService.getCurrentUser()?.id
                ?: return Resource.Error("User not logged in")

            val collectionDto = collectionService.getCollectionById(id)
            val count = collectionService.getCollectionQuoteCount(userId, id) // ✅ FIX: Pass userId
            Resource.Success(collectionDto.toCollection(count))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch collection")
        }
    }

    override suspend fun updateCollection(id: String, name: String): Resource<Unit> {
        return try {
            collectionService.updateCollection(id, name)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update collection")
        }
    }

    override suspend fun deleteCollection(id: String): Resource<Unit> {
        return try {
            collectionService.deleteCollection(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete collection")
        }
    }

    override suspend fun addQuoteToCollection(collectionId: String, quoteId: String): Resource<Unit> {
        return try {
            val userId = authService.getCurrentUser()?.id
                ?: return Resource.Error("User not logged in")

            android.util.Log.d("CollectionRepo", "═══ addQuoteToCollection START ═══")
            android.util.Log.d("CollectionRepo", "userId: $userId")
            android.util.Log.d("CollectionRepo", "collectionId: $collectionId")
            android.util.Log.d("CollectionRepo", "quoteId: $quoteId")

            collectionService.addQuoteToCollection(userId, collectionId, quoteId) // ✅ FIX: Pass userId

            android.util.Log.d("CollectionRepo", "✅ Successfully added quote to collection")
            Resource.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("CollectionRepo", "❌ Failed to add quote: ${e.message}", e)

            // Check for duplicate error
            val errorMsg = when {
                e.message?.contains("duplicate", ignoreCase = true) == true ->
                    "Quote is already in this collection"
                e.message?.contains("violates", ignoreCase = true) == true ->
                    "Quote is already in this collection"
                else -> e.message ?: "Failed to add quote to collection"
            }

            Resource.Error(errorMsg)
        }
    }

    override suspend fun removeQuoteFromCollection(collectionId: String, quoteId: String): Resource<Unit> {
        return try {
            val userId = authService.getCurrentUser()?.id
                ?: return Resource.Error("User not logged in")

            android.util.Log.d("CollectionRepo", "═══ removeQuoteFromCollection START ═══")
            collectionService.removeQuoteFromCollection(userId, collectionId, quoteId) // ✅ FIX: Pass userId
            android.util.Log.d("CollectionRepo", "✅ Successfully removed quote from collection")
            Resource.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("CollectionRepo", "❌ Failed to remove quote: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to remove quote from collection")
        }
    }

    override suspend fun getCollectionQuotes(collectionId: String): Resource<List<Quote>> {
        return try {
            val userId = authService.getCurrentUser()?.id
                ?: return Resource.Error("User not logged in")

            android.util.Log.d("CollectionRepo", "═══ getCollectionQuotes START ═══")
            android.util.Log.d("CollectionRepo", "userId: $userId, collectionId: $collectionId")

            val quotes = collectionService.getCollectionQuotes(userId, collectionId) // ✅ FIX: Pass userId

            android.util.Log.d("CollectionRepo", "✅ Fetched ${quotes.size} quotes")
            Resource.Success(quotes.toQuotes())
        } catch (e: Exception) {
            android.util.Log.e("CollectionRepo", "❌ Failed to fetch quotes: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to fetch collection quotes")
        }
    }

    override fun getCollectionsFlow(): Flow<List<Collection>> = flow {
        try {
            val userId = authService.getCurrentUser()?.id ?: return@flow
            val collections = collectionService.getCollections(userId)
            emit(collections.toCollections())
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
