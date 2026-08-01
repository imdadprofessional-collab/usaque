package com.cdlpermitprep.usa.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cdlpermitprep.usa.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun signInAnonymously() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            val result = userRepository.signInAnonymously()
            _uiState.value = result.fold(
                onSuccess = { LoginUiState(success = true) },
                onFailure = { LoginUiState(error = it.message ?: "Sign-in failed") },
            )
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            val result = userRepository.signInWithEmail(email, password)
            _uiState.value = result.fold(
                onSuccess = { LoginUiState(success = true) },
                onFailure = { LoginUiState(error = it.message ?: "Sign-in failed") },
            )
        }
    }

    fun skipForNow() {
        _uiState.value = LoginUiState(success = true)
    }
}
