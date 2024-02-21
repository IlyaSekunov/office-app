package ru.ilyasekunov.officeapp.ui.userprofile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.User
import ru.ilyasekunov.officeapp.data.repository.user.UserRepository
import javax.inject.Inject

data class UserProfileUiState(
    val name: String = "",
    val surname: String = "",
    val job: String = "",
    val photo: Any? = null,
    val currentOffice: Office? = null,
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false
)

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    var userProfileUiState by mutableStateOf(UserProfileUiState())
        private set

    init {
        loadUserProfile()
    }

    private fun updateIsLoading(isLoading: Boolean) {
        userProfileUiState = userProfileUiState.copy(isLoading = isLoading)
    }

    private fun updateIsLoggedOut(isLoggedOut: Boolean) {
        userProfileUiState = userProfileUiState.copy(isLoggedOut = isLoggedOut)
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            updateIsLoading(true)
            val user = userRepository.user()!!
            userProfileUiState = user.toUserProfileUiState()
            updateIsLoading(false)
        }
    }

    fun logout() {
        updateIsLoggedOut(true)
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