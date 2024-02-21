package ru.ilyasekunov.officeapp.ui.userprofile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.dto.UserDto
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.repository.user.UserRepository
import javax.inject.Inject

data class UserManageAccountUiState(
    val currentUserProfileUiState: UserProfileUiState = UserProfileUiState(),
    val mutableUserProfileUiState: UserProfileUiState = UserProfileUiState(),
    val availableOffices: List<Office> = emptyList(),
    val isLoading: Boolean = false,
    val isChangesSaved: Boolean = false
)

@HiltViewModel
class UserManageAccountViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    var userManageAccountUiState by mutableStateOf(UserManageAccountUiState())
        private set

    init {
        loadUserProfile()
        loadAvailableOffices()
    }

    fun updateName(name: String) {
        val userProfileUiState = userManageAccountUiState.mutableUserProfileUiState
        userManageAccountUiState = userManageAccountUiState.copy(
            mutableUserProfileUiState = userProfileUiState.copy(name = name)
        )
    }

    fun updateSurname(surname: String) {
        val userProfileUiState = userManageAccountUiState.mutableUserProfileUiState
        userManageAccountUiState = userManageAccountUiState.copy(
            mutableUserProfileUiState = userProfileUiState.copy(surname = surname)
        )
    }

    fun updateJob(job: String) {
        val userProfileUiState = userManageAccountUiState.mutableUserProfileUiState
        userManageAccountUiState = userManageAccountUiState.copy(
            mutableUserProfileUiState = userProfileUiState.copy(job = job)
        )
    }

    fun updateOffice(office: Office) {
        val userProfileUiState = userManageAccountUiState.mutableUserProfileUiState
        userManageAccountUiState = userManageAccountUiState.copy(
            mutableUserProfileUiState = userProfileUiState.copy(currentOffice = office)
        )
    }

    fun updatePhoto(photo: Any?) {
        val userProfileUiState = userManageAccountUiState.mutableUserProfileUiState
        userManageAccountUiState = userManageAccountUiState.copy(
            mutableUserProfileUiState = userProfileUiState.copy(photo = photo)
        )
    }

    fun save() {
        viewModelScope.launch {
            updateIsLoading(true)
            val userProfileUiState = userManageAccountUiState.mutableUserProfileUiState
            userRepository.saveChanges(userProfileUiState.toUserDto())
            updateIsLoading(false)
            updateIsChangesSaved(true)
        }
    }

    private fun updateAvailableOffices(availableOffices: List<Office>) {
        userManageAccountUiState = userManageAccountUiState.copy(availableOffices = availableOffices)
    }

    private fun updateIsLoading(isLoading: Boolean) {
        userManageAccountUiState = userManageAccountUiState.copy(isLoading = isLoading)
    }

    private fun updateIsChangesSaved(isChangesSaved: Boolean) {
        userManageAccountUiState = userManageAccountUiState.copy(isChangesSaved = isChangesSaved)
    }

    private fun updateCurrentUserProfileUiState(userProfileUiState: UserProfileUiState) {
        userManageAccountUiState = userManageAccountUiState.copy(
            currentUserProfileUiState = userProfileUiState
        )
    }

    private fun updateMutableUserProfileUiState(userProfileUiState: UserProfileUiState) {
        userManageAccountUiState = userManageAccountUiState.copy(
            mutableUserProfileUiState = userProfileUiState
        )
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            updateIsLoading(true)
            val user = userRepository.user()!!
            val userProfileUiState = user.toUserProfileUiState()
            updateCurrentUserProfileUiState(userProfileUiState)
            updateMutableUserProfileUiState(userProfileUiState)
            updateIsLoading(false)
        }
    }

    private fun loadAvailableOffices() {
        viewModelScope.launch {
            updateIsLoading(true)
            val availableOffices = userRepository.availableOffices()
            if (availableOffices.isNotEmpty()) {
                updateAvailableOffices(availableOffices)
                if (userManageAccountUiState.mutableUserProfileUiState.currentOffice == null) {
                    updateOffice(userManageAccountUiState.availableOffices[0])
                }
            }
            updateIsLoading(false)
        }
    }
}

fun UserProfileUiState.toUserDto(): UserDto =
    UserDto(
        name = name,
        surname = surname,
        job = job,
        officeId = currentOffice!!.id,
        photo = photo
    )