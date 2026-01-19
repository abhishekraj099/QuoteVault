package com.example.quotevault.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class QuoteDto(
    val id: String,
    val text: String,
    val author: String,
    val category: String,
    val created_at: String? = null
)
