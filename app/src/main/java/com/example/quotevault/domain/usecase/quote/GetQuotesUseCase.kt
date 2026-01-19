package com.example.quotevault.domain.usecase.quote

import com.example.quotevault.core.util.Resource
import com.example.quotevault.domain.model.Quote
import com.example.quotevault.domain.repository.QuoteRepository
import javax.inject.Inject

class GetQuotesUseCase @Inject constructor(
    private val quoteRepository: QuoteRepository
) {
    suspend operator fun invoke(page: Int, limit: Int): Resource<List<Quote>> {
        return quoteRepository.getQuotes(page, limit)
    }
}
