package com.example.quotevault.domain.usecase.auth

import com.example.quotevault.core.util.Resource
import com.example.quotevault.domain.model.User
import com.example.quotevault.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, name: String): Resource<User> {
        if (email.isBlank()) {
            return Resource.Error("Email cannot be empty")
        }
        if (password.isBlank()) {
            return Resource.Error("Password cannot be empty")
        }
        if (password.length < 6) {
            return Resource.Error("Password must be at least 6 characters")
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Resource.Error("Invalid email format")
        }
        if (name.isBlank()) {
            return Resource.Error("Name cannot be empty")
        }
        return authRepository.signUp(email, password, name)
    }
}
