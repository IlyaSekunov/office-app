package ru.ilyasekunov.officeapp.ui.auth.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.dto.LoginForm
import ru.ilyasekunov.officeapp.data.repository.auth.AuthRepository
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    var loginUiState by mutableStateOf(LoginUiState())
        private set

    fun updateEmail(email: String) {
        loginUiState = loginUiState.copy(email = email)
    }

    fun updatePassword(password: String) {
        loginUiState = loginUiState.copy(password = password)
    }

    fun login() {
        viewModelScope.launch {
            updateIsLoading(true)
            //authRepository.login(loginUiState.toLoginForm())
            updateIsLoading(false)
            updateIsLoggedIn(true)
        }
    }

    private fun updateIsLoading(isLoading: Boolean) {
        loginUiState = loginUiState.copy(isLoading = isLoading)
    }

    private fun updateIsLoggedIn(isLoggedIn: Boolean) {
        loginUiState = loginUiState.copy(isLoggedIn = isLoggedIn)
    }
}

fun LoginUiState.toLoginForm() = LoginForm(email, password)