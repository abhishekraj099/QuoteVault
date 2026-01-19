package com.example.quotevault.domain.repository

import com.example.quotevault.core.util.Resource
import com.example.quotevault.domain.model.User

interface AuthRepository {
    suspend fun signUp(email: String, password: String, name: String): Resource<User>
    suspend fun login(email: String, password: String): Resource<User>
    suspend fun logout(): Resource<Unit>
    suspend fun resetPassword(email: String): Resource<Unit>
    suspend fun getCurrentUser(): User?
    suspend fun isUserLoggedIn(): Boolean
}
