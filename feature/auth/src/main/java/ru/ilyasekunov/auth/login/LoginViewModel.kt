package ru.ilyasekunov.auth.login

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.auth.repository.AuthRepository
import ru.ilyasekunov.dto.LoginForm
import ru.ilyasekunov.network.exceptions.IncorrectCredentialsException
import ru.ilyasekunov.validation.EmailValidationError
import ru.ilyasekunov.validation.EmailValidationResult
import ru.ilyasekunov.validation.PasswordValidationError
import ru.ilyasekunov.validation.PasswordValidationResult
import ru.ilyasekunov.validation.validateEmail
import ru.ilyasekunov.validation.validatePassword
import javax.inject.Inject

@Immutable
data class LoginUiState(
    val emailUiState: EmailUiState = EmailUiState(),
    val passwordUiState: PasswordUiState = PasswordUiState(),
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val credentialsInvalid: Boolean = false,
    val isNetworkError: Boolean = false
)

@Immutable
data class EmailUiState(
    val email: String = "",
    val error: EmailValidationError? = null
)

@Immutable
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

    fun invalidCredentialsShown() {
        loginUiState = loginUiState.copy(credentialsInvalid = false)
    }

    fun networkErrorShown() {
        loginUiState = loginUiState.copy(isNetworkError = false)
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
                loginUiState = loginUiState.copy(isLoading = true)
                authRepository.login(loginUiState.toLoginForm()).also { result ->
                    val exception = result.exceptionOrNull()
                    loginUiState = loginUiState.copy(
                        isLoading = false,
                        isLoggedIn = result.isSuccess,
                        credentialsInvalid = exception is IncorrectCredentialsException,
                        isNetworkError = exception !is IncorrectCredentialsException,
                    )
                }
            }
        }
    }

    private fun credentialsValid(): Boolean {
        val isEmailValid = isEmailValid()
        val isPasswordValid = isPasswordValid()
        return isEmailValid && isPasswordValid
    }

    private fun isEmailValid(): Boolean {
        val emailValidationResult = validateEmail(loginUiState.emailUiState.email)
        return if (emailValidationResult is EmailValidationResult.Failure) {
            updateEmailValidationError(emailValidationResult.error)
            false
        } else {
            updateEmailValidationError(null)
            true
        }
    }

    private fun isPasswordValid(): Boolean {
        val passwordValidationResult = validatePassword(loginUiState.passwordUiState.password)
        return if (passwordValidationResult is PasswordValidationResult.Failure) {
            updatePasswordValidationError(passwordValidationResult.error)
            false
        } else {
            updatePasswordValidationError(null)
            true
        }
    }
}

fun LoginUiState.toLoginForm() = LoginForm(emailUiState.email, passwordUiState.password)