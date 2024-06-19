package ru.ilyasekunov.profile

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.auth.registration.UserInfoFieldUiState
import ru.ilyasekunov.auth.repository.AuthRepository
import ru.ilyasekunov.model.Office
import ru.ilyasekunov.model.User
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
            userProfileUiState = userProfileUiState.copy(isLoading = true)
            authRepository.logout()
            userProfileUiState = userProfileUiState.copy(
                isLoggedOut = true,
                isLoading = false
            )
        }
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            userProfileUiState = userProfileUiState.copy(isLoading = true)
            refreshUserProfile()
            userProfileUiState = userProfileUiState.copy(isLoading = false)
        }
    }

    suspend fun refreshUserProfile() {
        authRepository.userInfo().also { result ->
            userProfileUiState = userProfileUiState.copy(
                isErrorWhileUserLoading = result.isFailure
            )
            if (result.isSuccess) {
                val user = result.getOrThrow()
                userProfileUiState = user.toUserProfileUiState()
            }
        }
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