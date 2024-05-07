package ru.ilyasekunov.officeapp.ui.auth.login

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.dto.LoginForm
import ru.ilyasekunov.officeapp.data.repository.auth.AuthRepository
import ru.ilyasekunov.officeapp.exceptions.IncorrectCredentialsException
import ru.ilyasekunov.officeapp.validation.EmailValidationError
import ru.ilyasekunov.officeapp.validation.EmailValidationResult
import ru.ilyasekunov.officeapp.validation.PasswordValidationError
import ru.ilyasekunov.officeapp.validation.PasswordValidationResult
import ru.ilyasekunov.officeapp.validation.validateEmail
import ru.ilyasekunov.officeapp.validation.validatePassword
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
                    loginUiState = loginUiState.copy(
                        isLoading = false,
                        isLoggedIn = result.isSuccess
                    )

                    when {
                        result.isSuccess -> {
                            loginUiState = loginUiState.copy(
                                credentialsInvalid = false,
                                isNetworkError = false
                            )
                        }

                        result.exceptionOrNull()!! is IncorrectCredentialsException -> {
                            loginUiState = loginUiState.copy(
                                credentialsInvalid = true,
                                isNetworkError = false
                            )
                        }

                        else -> {
                            loginUiState = loginUiState.copy(
                                isNetworkError = true,
                                credentialsInvalid = false
                            )
                        }
                    }
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