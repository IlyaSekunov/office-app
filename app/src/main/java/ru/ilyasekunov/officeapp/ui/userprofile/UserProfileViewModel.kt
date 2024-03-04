package ru.ilyasekunov.officeapp.ui.userprofile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.User
import ru.ilyasekunov.officeapp.data.repository.auth.AuthRepository
import ru.ilyasekunov.officeapp.data.repository.user.UserRepository
import javax.inject.Inject

data class UserProfileUiState(
    val name: String = "",
    val surname: String = "",
    val job: String = "",
    val photo: Any? = null,
    val currentOffice: Office? = null,
    val isLoading: Boolean = false,
    val isErrorWhileUserLoading: Boolean = false,
    val isErrorWhileLoggingOut: Boolean = false,
    val isLoggedOut: Boolean = false
)

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    var userProfileUiState by mutableStateOf(UserProfileUiState())
        private set

    init {
        loadUserProfile()
    }

    fun logout() {
        viewModelScope.launch {
            updateIsLoading(true)
            val logoutResult = authRepository.logout()
            if (logoutResult.isSuccess) {
                updateIsErrorWhileLoggingOut(false)
                updateIsLoggedOut(true)
            } else {
                updateIsErrorWhileLoggingOut(true)
            }
            updateIsLoading(false)
        }
    }

    fun refreshUserProfile(): Job = viewModelScope.launch {
        loadUserProfileSuspending()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            updateIsLoading(true)
            loadUserProfileSuspending()
            updateIsLoading(false)
        }
    }

    private suspend fun loadUserProfileSuspending() {
        val userResult = userRepository.user()
        delay(3000)
        if (userResult.isSuccess) {
            val user = userResult.getOrThrow()!!
            userProfileUiState = user.toUserProfileUiState()
            updateIsErrorWhileUserLoading(false)
        } else {
            updateIsErrorWhileUserLoading(true)
        }
    }

    private fun updateIsLoading(isLoading: Boolean) {
        userProfileUiState = userProfileUiState.copy(isLoading = isLoading)
    }

    private fun updateIsLoggedOut(isLoggedOut: Boolean) {
        userProfileUiState = userProfileUiState.copy(isLoggedOut = isLoggedOut)
    }

    private fun updateIsErrorWhileUserLoading(isErrorWhileUserLoading: Boolean) {
        userProfileUiState = userProfileUiState.copy(
            isErrorWhileUserLoading = isErrorWhileUserLoading
        )
    }

    private fun updateIsErrorWhileLoggingOut(isErrorWhileLoggingOut: Boolean) {
        userProfileUiState = userProfileUiState.copy(
            isErrorWhileLoggingOut = isErrorWhileLoggingOut
        )
    }
}

fun User.toUserProfileUiState(): UserProfileUiState =
    UserProfileUiState(
        name = name,
        surname = surname,
        job = job,
        photo = photo,
        currentOffice = office
    )