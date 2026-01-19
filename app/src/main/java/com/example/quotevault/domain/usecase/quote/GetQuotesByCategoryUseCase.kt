package com.example.quotevault.domain.usecase.quote

import com.example.quotevault.core.util.Resource
import com.example.quotevault.domain.model.Quote
import com.example.quotevault.domain.repository.QuoteRepository
import javax.inject.Inject

class GetQuotesByCategoryUseCase @Inject constructor(
    private val quoteRepository: QuoteRepository
) {
    suspend operator fun invoke(category: String, page: Int, limit: Int): Resource<List<Quote>> {
        return quoteRepository.getQuotesByCategory(category, page, limit)
    }
}
