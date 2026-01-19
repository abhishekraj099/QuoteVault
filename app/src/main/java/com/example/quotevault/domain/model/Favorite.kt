package com.example.quotevault.domain.model

data class Favorite(
    val id: String,
    val userId: String,
    val quoteId: String,
    val createdAt: Long
)
