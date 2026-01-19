package com.example.quotevault.presentation.profile

import com.example.quotevault.domain.model.User

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
