package com.example.quotevault.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CollectionDto(
    val id: String,
    val user_id: String,
    val name: String,
    val created_at: String
)

@Serializable
data class CollectionItemDto(
    val id: String,
    val collection_id: String,
    val quote_id: String,
    val created_at: String
)
