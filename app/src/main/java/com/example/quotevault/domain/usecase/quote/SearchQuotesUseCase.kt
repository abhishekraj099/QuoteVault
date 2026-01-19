package com.example.quotevault.domain.usecase.quote

import com.example.quotevault.core.util.Resource
import com.example.quotevault.domain.model.Quote
import com.example.quotevault.domain.repository.QuoteRepository
import javax.inject.Inject

class SearchQuotesUseCase @Inject constructor(
    private val quoteRepository: QuoteRepository
) {
    suspend operator fun invoke(query: String): Resource<List<Quote>> {
        if (query.isBlank()) {
            return Resource.Error("Search query cannot be empty")
        }
        return quoteRepository.searchQuotes(query)
    }
}
