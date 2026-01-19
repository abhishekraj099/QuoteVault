package com.example.quotevault.domain.usecase.quote

import com.example.quotevault.core.util.Resource
import com.example.quotevault.domain.model.Quote
import com.example.quotevault.domain.repository.QuoteRepository
import javax.inject.Inject

class GetQuoteOfTheDayUseCase @Inject constructor(
    private val quoteRepository: QuoteRepository
) {
    suspend operator fun invoke(): Resource<Quote> {
        return quoteRepository.getQuoteOfTheDay()
    }
}
