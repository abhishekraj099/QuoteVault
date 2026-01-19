package com.example.quotevault.data.remote.mapper

import com.example.quotevault.data.remote.dto.QuoteDto
import com.example.quotevault.domain.model.Quote

fun QuoteDto.toQuote(): Quote {
    return Quote(
        id = id,
        text = text,
        author = author,
        category = category,
        isFavorite = false
    )
}

fun List<QuoteDto>.toQuotes(): List<Quote> {
    return map { it.toQuote() }
}
