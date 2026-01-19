package com.example.quotevault.domain.repository

import com.example.quotevault.core.util.Resource
import com.example.quotevault.domain.model.Collection
import com.example.quotevault.domain.model.Quote
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {
    suspend fun createCollection(name: String): Resource<Collection>
    suspend fun getCollections(): Resource<List<Collection>>
    suspend fun getCollectionById(id: String): Resource<Collection>
    suspend fun updateCollection(id: String, name: String): Resource<Unit>
    suspend fun deleteCollection(id: String): Resource<Unit>
    suspend fun addQuoteToCollection(collectionId: String, quoteId: String): Resource<Unit>
    suspend fun removeQuoteFromCollection(collectionId: String, quoteId: String): Resource<Unit>
    suspend fun getCollectionQuotes(collectionId: String): Resource<List<Quote>>
    fun getCollectionsFlow(): Flow<List<Collection>>
}
