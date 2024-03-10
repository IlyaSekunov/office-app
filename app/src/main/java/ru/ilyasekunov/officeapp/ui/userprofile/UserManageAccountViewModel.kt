package ru.ilyasekunov.officeapp.ui.userprofile

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.dto.UserDto
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.repository.auth.AuthRepository
import ru.ilyasekunov.officeapp.data.repository.images.ImagesRepository
import ru.ilyasekunov.officeapp.data.repository.user.UserRepository
import ru.ilyasekunov.officeapp.validation.UserInfoValidationError
import ru.ilyasekunov.officeapp.validation.UserInfoValidationResult
import ru.ilyasekunov.officeapp.validation.validateUserInfo
import javax.inject.Inject

data class UserManageAccountUiState(
    val currentUserProfileUiState: UserProfileUiState = UserProfileUiState(),
    val mutableUserProfileUiState: UserProfileUiState = UserProfileUiState(),
    val availableOffices: List<Office> = emptyList(),
    val isLoading: Boolean = false,
    val isChangesSaved: Boolean = false,
    val isNetworkError: Boolean = false
)

@HiltViewModel
class UserManageAccountViewModel @Inject constructor(
    private val authRepository: AuthRepository,
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
            mutableUserProfileUiState = userProfileUiState.copy(
                name = userProfileUiState.name.copy(value = name)
            )
        )
    }

    fun updateSurname(surname: String) {
        val userProfileUiState = userManageAccountUiState.mutableUserProfileUiState
        userManageAccountUiState = userManageAccountUiState.copy(
            mutableUserProfileUiState = userProfileUiState.copy(
                surname = userProfileUiState.surname.copy(value = surname)
            )
        )
    }

    fun updateJob(job: String) {
        val userProfileUiState = userManageAccountUiState.mutableUserProfileUiState
        userManageAccountUiState = userManageAccountUiState.copy(
            mutableUserProfileUiState = userProfileUiState.copy(
                job = userProfileUiState.job.copy(value = job)
            )
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
        if (userInfoValid()) {
            viewModelScope.launch {
                updateIsLoading(true)
                val userProfileUiState = userManageAccountUiState.mutableUserProfileUiState
                val photoUrlResult = uploadUserPhoto()
                if (photoUrlResult.isFailure) {
                    updateIsNetworkError(true)
                    updateIsLoading(false)
                    return@launch
                } else {
                    updateIsNetworkError(false)
                }
                val photoUrl = photoUrlResult.getOrThrow()
                val userDto = UserDto(
                    name = userProfileUiState.name.value,
                    surname = userProfileUiState.surname.value,
                    job = userProfileUiState.job.value,
                    photo = photoUrl,
                    officeId = userProfileUiState.currentOffice!!.id
                )
                userRepository.saveChanges(userDto)
                updateIsNetworkError(false)
                updateIsLoading(false)
                updateIsChangesSaved(true)
            }
        }
    }

    private fun updateNameValidationError(error: UserInfoValidationError?) {
        val userProfileUiState = userManageAccountUiState.mutableUserProfileUiState
        userManageAccountUiState = userManageAccountUiState.copy(
            mutableUserProfileUiState = userProfileUiState.copy(
                name = userProfileUiState.name.copy(error = error)
            )
        )
    }

    private fun updateSurnameValidationError(error: UserInfoValidationError?) {
        val userProfileUiState = userManageAccountUiState.mutableUserProfileUiState
        userManageAccountUiState = userManageAccountUiState.copy(
            mutableUserProfileUiState = userProfileUiState.copy(
                surname = userProfileUiState.surname.copy(error = error)
            )
        )
    }

    private fun updateJobValidationError(error: UserInfoValidationError?) {
        val userProfileUiState = userManageAccountUiState.mutableUserProfileUiState
        userManageAccountUiState = userManageAccountUiState.copy(
            mutableUserProfileUiState = userProfileUiState.copy(
                job = userProfileUiState.job.copy(error = error)
            )
        )
    }

    fun userInfoValid(): Boolean {
        val userProfileUiState = userManageAccountUiState.mutableUserProfileUiState
        var isValidationSuccess = true

        val name = userProfileUiState.name.value
        val nameValidationResult = validateUserInfo(name)
        if (nameValidationResult is UserInfoValidationResult.Failure) {
            updateNameValidationError(nameValidationResult.error)
            isValidationSuccess = false
        } else {
            updateNameValidationError(null)
        }

        val surname = userProfileUiState.surname.value
        val surnameValidationResult = validateUserInfo(surname)
        if (surnameValidationResult is UserInfoValidationResult.Failure) {
            updateSurnameValidationError(surnameValidationResult.error)
            isValidationSuccess = false
        } else {
            updateSurnameValidationError(null)
        }

        val job = userProfileUiState.job.value
        val jobValidationResult = validateUserInfo(job)
        if (jobValidationResult is UserInfoValidationResult.Failure) {
            updateJobValidationError(jobValidationResult.error)
            isValidationSuccess = false
        } else {
            updateJobValidationError(null)
        }

        return isValidationSuccess
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

    private fun updateIsNetworkError(isNetworkError: Boolean) {
        userManageAccountUiState = userManageAccountUiState.copy(
            isNetworkError = isNetworkError
        )
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            updateIsLoading(true)
            val userResult = authRepository.userInfo()
            if (userResult.isSuccess) {
                val user = userResult.getOrThrow()
                val userProfileUiState = user.toUserProfileUiState()
                updateCurrentUserProfileUiState(userProfileUiState)
                updateMutableUserProfileUiState(userProfileUiState)
            } else {
                updateIsNetworkError(true)
            }
            updateIsLoading(false)
        }
    }

    private fun loadAvailableOffices() {
        viewModelScope.launch {
            updateIsLoading(true)
            val availableOfficesResult = userRepository.availableOffices()
            if (availableOfficesResult.isSuccess) {
                val availableOffices = availableOfficesResult.getOrThrow()
                if (availableOffices.isNotEmpty()) {
                    updateAvailableOffices(availableOffices)
                    if (userManageAccountUiState.mutableUserProfileUiState.currentOffice == null) {
                        updateOffice(userManageAccountUiState.availableOffices[0])
                    }
                }
            } else {
                updateIsNetworkError(true)
            }
            updateIsLoading(false)
        }
    }

    private suspend fun uploadUserPhoto(): Result<String?> {
        val pickedPhoto = userManageAccountUiState.mutableUserProfileUiState.photo
        return if (pickedPhoto is Uri) {
            val photoUrlResult = imagesRepository.uploadImage(pickedPhoto)
            if (photoUrlResult.isSuccess) {
                val photoUrl = photoUrlResult.getOrThrow()
                Result.success(photoUrl)
            } else {
                photoUrlResult
            }
        } else if (pickedPhoto is String) {
            Result.success(pickedPhoto)
        } else {
            Result.success(null)
        }
    }
}