package com.example.quotevault.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FavoriteDto(
    val id: String,
    val user_id: String,
    val quote_id: String,
    val created_at: String
)
