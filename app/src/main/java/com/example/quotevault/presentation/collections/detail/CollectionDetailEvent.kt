package com.example.quotevault.presentation.collections.detail

sealed class CollectionDetailEvent {
    data class RemoveQuote(val quoteId: String) : CollectionDetailEvent()
    object Refresh : CollectionDetailEvent()
}
