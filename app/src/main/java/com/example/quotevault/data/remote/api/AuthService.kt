package com.example.quotevault.data.remote.api

import com.example.quotevault.data.remote.dto.UserDto
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import javax.inject.Inject

class AuthService @Inject constructor(
    private val supabaseClient: SupabaseClientProvider
) {
    private val auth: Auth get() = supabaseClient.client.auth

    suspend fun signUp(email: String, password: String, name: String): UserDto {
        val result = auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }

        return UserDto(
            id = result?.id ?: throw Exception("User ID is null"),
            email = email,
            user_metadata = null
        )
    }

    suspend fun login(email: String, password: String): UserDto {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }

        val user = auth.currentUserOrNull() ?: throw Exception("Login failed")

        return UserDto(
            id = user.id,
            email = user.email ?: email,
            user_metadata = null
        )
    }

    suspend fun logout() {
        auth.signOut()
    }

    suspend fun resetPassword(email: String) {
        auth.resetPasswordForEmail(email)
    }

    suspend fun getCurrentUser(): UserDto? {
        val user = auth.currentUserOrNull() ?: return null
        return UserDto(
            id = user.id,
            email = user.email ?: "",
            user_metadata = null
        )
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUserOrNull() != null
    }
}
