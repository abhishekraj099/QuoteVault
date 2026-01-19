package com.example.quotevault.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotevault.core.util.Resource
import com.example.quotevault.core.util.UiEvent
import com.example.quotevault.domain.usecase.auth.GetCurrentUserUseCase
import com.example.quotevault.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadUser()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.Logout -> {
                logout()
            }
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val user = getCurrentUserUseCase()
            _state.update {
                it.copy(
                    user = user,
                    isLoading = false
                )
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            when (logoutUseCase()) {
                is Resource.Success -> {
                    _uiEvent.send(UiEvent.Navigate("login"))
                }
                is Resource.Error -> {
                    _uiEvent.send(UiEvent.ShowSnackbar("Logout failed"))
                }
                is Resource.Loading -> {}
            }
        }
    }
}

sealed class ProfileEvent {
    object Logout : ProfileEvent()
}
