package ru.ilyasekunov.officeapp.ui.userprofile

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.ResponseResult
import ru.ilyasekunov.officeapp.data.dto.UserDto
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.repository.images.ImagesRepository
import ru.ilyasekunov.officeapp.data.repository.user.UserRepository
import javax.inject.Inject

data class UserManageAccountUiState(
    val currentUserProfileUiState: UserProfileUiState = UserProfileUiState(),
    val mutableUserProfileUiState: UserProfileUiState = UserProfileUiState(),
    val availableOffices: List<Office> = emptyList(),
    val isLoading: Boolean = false,
    val isChangesSaved: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class UserManageAccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val imagesRepository: ImagesRepository
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

    fun updatePhoto(photo: Uri?) {
        val userProfileUiState = userManageAccountUiState.mutableUserProfileUiState
        userManageAccountUiState = userManageAccountUiState.copy(
            mutableUserProfileUiState = userProfileUiState.copy(photo = photo)
        )
    }

    fun save() {
        viewModelScope.launch {
            updateIsLoading(true)
            val userProfileUiState = userManageAccountUiState.mutableUserProfileUiState
            val uploadedUserPhoto = uploadUserPhoto()
            if (userManageAccountUiState.errorMessage != null) {
                updateIsLoading(false)
                return@launch
            }

            val userDto = UserDto(
                name = userProfileUiState.name,
                surname = userProfileUiState.surname,
                job = userProfileUiState.job,
                photo = uploadedUserPhoto,
                officeId = userProfileUiState.currentOffice!!.id
            )
            userRepository.saveChanges(userDto)
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

    private fun updateErrorMessage(errorMessage: String?) {
        userManageAccountUiState = userManageAccountUiState.copy(
            errorMessage = errorMessage
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

    private suspend fun uploadUserPhoto(): String? {
        val pickedPhoto = userManageAccountUiState.mutableUserProfileUiState.photo
        return pickedPhoto?.let {
            when (val response = imagesRepository.uploadImage(it as Uri)) {
                is ResponseResult.Success -> {
                    response.value
                }
                is ResponseResult.Failure -> {
                    updateErrorMessage(response.message)
                    return null
                }
            }
        }
    }
}