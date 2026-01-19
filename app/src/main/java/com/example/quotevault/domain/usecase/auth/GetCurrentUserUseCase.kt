package com.example.quotevault.domain.usecase.auth

import com.example.quotevault.domain.model.User
import com.example.quotevault.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): User? {
        return authRepository.getCurrentUser()
    }
}
