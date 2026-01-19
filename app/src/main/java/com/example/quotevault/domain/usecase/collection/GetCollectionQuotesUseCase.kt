package com.example.quotevault.domain.usecase.collection

import com.example.quotevault.core.util.Resource
import com.example.quotevault.domain.model.Quote
import com.example.quotevault.domain.repository.CollectionRepository
import javax.inject.Inject

class GetCollectionQuotesUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository
) {
    suspend operator fun invoke(collectionId: String): Resource<List<Quote>> {
        return collectionRepository.getCollectionQuotes(collectionId)
    }
}
