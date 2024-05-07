package ru.ilyasekunov.officeapp.ui.userprofile

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.User
import ru.ilyasekunov.officeapp.data.repository.auth.AuthRepository
import ru.ilyasekunov.officeapp.ui.auth.registration.UserInfoFieldUiState
import javax.inject.Inject

@Immutable
data class UserProfileUiState(
    val name: UserInfoFieldUiState = UserInfoFieldUiState(),
    val surname: UserInfoFieldUiState = UserInfoFieldUiState(),
    val job: UserInfoFieldUiState = UserInfoFieldUiState(),
    val photo: Any? = null,
    val currentOffice: Office? = null,
    val isLoading: Boolean = false,
    val isErrorWhileUserLoading: Boolean = false,
    val isLoggedOut: Boolean = false
)

@HiltViewModel
class UserProfileViewModel @Inject constructor(
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
            authRepository.logout()
            userProfileUiState = userProfileUiState.copy(isLoggedOut = true)
            updateIsLoading(false)
        }
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            updateIsLoading(true)
            refreshUserProfile()
            updateIsLoading(false)
        }
    }

    suspend fun refreshUserProfile() {
        val userResult = authRepository.userInfo()
        if (userResult.isSuccess) {
            val user = userResult.getOrThrow()
            userProfileUiState = user.toUserProfileUiState()
            updateIsErrorWhileUserLoading(false)
        } else {
            updateIsErrorWhileUserLoading(true)
        }
    }

    private fun updateIsLoading(isLoading: Boolean) {
        userProfileUiState = userProfileUiState.copy(isLoading = isLoading)
    }

    private fun updateIsErrorWhileUserLoading(isErrorWhileUserLoading: Boolean) {
        userProfileUiState = userProfileUiState.copy(
            isErrorWhileUserLoading = isErrorWhileUserLoading
        )
    }
}

fun User.toUserProfileUiState(): UserProfileUiState =
    UserProfileUiState(
        name = UserInfoFieldUiState(value = name),
        surname = UserInfoFieldUiState(value = surname),
        job = UserInfoFieldUiState(value = job),
        photo = photo,
        currentOffice = office
    )