package edu.cit.cortes.stockmate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.cit.cortes.stockmate.data.AuthRepository
import edu.cit.cortes.stockmate.model.AuthResponse
import edu.cit.cortes.stockmate.model.LoginRequest
import edu.cit.cortes.stockmate.model.RegisterRequest
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableLiveData<AuthUiState>()
    val uiState: LiveData<AuthUiState> = _uiState

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.register(request).fold(
                onSuccess = { _uiState.value = AuthUiState.RegistrationSuccess(it.message) },
                onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Registration failed") }
            )
        }
    }

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.login(request).fold(
                onSuccess = { _uiState.value = AuthUiState.LoginSuccess(it) },
                onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Login failed") }
            )
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class RegistrationSuccess(val message: String) : AuthUiState()
    data class LoginSuccess(val authResponse: AuthResponse) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
