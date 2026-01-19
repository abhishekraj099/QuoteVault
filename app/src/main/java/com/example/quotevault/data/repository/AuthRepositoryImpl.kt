package com.example.quotevault.data.repository

import com.example.quotevault.core.util.Resource
import com.example.quotevault.data.remote.api.AuthService
import com.example.quotevault.data.remote.mapper.toUser
import com.example.quotevault.domain.model.User
import com.example.quotevault.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService
) : AuthRepository {

    override suspend fun signUp(email: String, password: String, name: String): Resource<User> {
        return try {
            val userDto = authService.signUp(email, password, name)
            Resource.Success(userDto.toUser())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred during sign up")
        }
    }

    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val userDto = authService.login(email, password)
            Resource.Success(userDto.toUser())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred during login")
        }
    }

    override suspend fun logout(): Resource<Unit> {
        return try {
            authService.logout()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred during logout")
        }
    }

    override suspend fun resetPassword(email: String): Resource<Unit> {
        return try {
            authService.resetPassword(email)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred during password reset")
        }
    }

    override suspend fun getCurrentUser(): User? {
        return try {
            authService.getCurrentUser()?.toUser()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return authService.isUserLoggedIn()
    }
}
