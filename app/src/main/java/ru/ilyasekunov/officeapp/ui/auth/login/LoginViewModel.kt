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
import ru.ilyasekunov.officeapp.validation.EmailValidationError
import ru.ilyasekunov.officeapp.validation.EmailValidationResult
import ru.ilyasekunov.officeapp.validation.PasswordValidationError
import ru.ilyasekunov.officeapp.validation.PasswordValidationResult
import ru.ilyasekunov.officeapp.validation.validateEmail
import ru.ilyasekunov.officeapp.validation.validatePassword
import javax.inject.Inject

data class LoginUiState(
    val emailUiState: EmailUiState = EmailUiState(),
    val passwordUiState: PasswordUiState = PasswordUiState(),
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false
)

data class EmailUiState(
    val email: String = "",
    val error: EmailValidationError? = null
)

data class PasswordUiState(
    val password: String = "",
    val error: PasswordValidationError? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    var loginUiState by mutableStateOf(LoginUiState())
        private set

    fun updateEmail(email: String) {
        loginUiState = loginUiState.copy(
            emailUiState = loginUiState.emailUiState.copy(email = email)
        )
    }

    fun updatePassword(password: String) {
        loginUiState = loginUiState.copy(
            passwordUiState = loginUiState.passwordUiState.copy(password = password)
        )
    }

    private fun updateEmailValidationError(error: EmailValidationError?) {
        loginUiState = loginUiState.copy(
            emailUiState = loginUiState.emailUiState.copy(error = error)
        )
    }

    private fun updatePasswordValidationError(error: PasswordValidationError?) {
        loginUiState = loginUiState.copy(
            passwordUiState = loginUiState.passwordUiState.copy(error = error)
        )
    }

    fun login() {
        viewModelScope.launch {
            if (credentialsValid()) {
                updateIsLoading(true)
                //authRepository.login(loginUiState.toLoginForm())
                updateIsLoading(false)
                updateIsLoggedIn(true)
            }
        }
    }

    private fun credentialsValid(): Boolean {
        var isValidationSuccess = true

        val emailValidationResult = validateEmail(loginUiState.emailUiState.email)
        if (emailValidationResult is EmailValidationResult.Failure) {
            updateEmailValidationError(emailValidationResult.error)
            isValidationSuccess = false
        } else {
            updateEmailValidationError(null)
        }

        val passwordValidationResult = validatePassword(loginUiState.passwordUiState.password)
        if (passwordValidationResult is PasswordValidationResult.Failure) {
            updatePasswordValidationError(passwordValidationResult.error)
            isValidationSuccess = false
        } else {
            updatePasswordValidationError(null)
        }

        return isValidationSuccess
    }

    private fun updateIsLoading(isLoading: Boolean) {
        loginUiState = loginUiState.copy(isLoading = isLoading)
    }

    private fun updateIsLoggedIn(isLoggedIn: Boolean) {
        loginUiState = loginUiState.copy(isLoggedIn = isLoggedIn)
    }
}

fun LoginUiState.toLoginForm() = LoginForm(emailUiState.email, passwordUiState.password)