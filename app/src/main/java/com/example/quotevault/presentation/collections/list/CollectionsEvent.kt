package com.example.quotevault.presentation.collections.list

sealed class CollectionsEvent {
    object ShowCreateDialog : CollectionsEvent()
    object HideCreateDialog : CollectionsEvent()
    data class CollectionNameChanged(val name: String) : CollectionsEvent()
    data class CreateCollection(val name: String) : CollectionsEvent()
    object CreateCollectionFromDialog : CollectionsEvent()
    data class AddQuoteToCollection(val collectionId: String, val quoteId: String) : CollectionsEvent()
    data class CreateCollectionAndAddQuote(val name: String, val quoteId: String) : CollectionsEvent()
    data class DeleteCollection(val collectionId: String) : CollectionsEvent()
    object Refresh : CollectionsEvent()
}

