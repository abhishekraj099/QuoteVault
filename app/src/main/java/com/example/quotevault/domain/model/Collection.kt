package com.example.quotevault.domain.model

data class Collection(
    val id: String,
    val userId: String,
    val name: String,
    val quoteCount: Int = 0,
    val createdAt: Long
)
