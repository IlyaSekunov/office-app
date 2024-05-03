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
import ru.ilyasekunov.officeapp.data.repository.office.OfficeRepository
import ru.ilyasekunov.officeapp.data.repository.user.UserRepository
import ru.ilyasekunov.officeapp.validation.UserInfoValidationError
import ru.ilyasekunov.officeapp.validation.UserInfoValidationResult
import ru.ilyasekunov.officeapp.validation.validateUserInfo
import javax.inject.Inject

data class UserManageAccountUiState(
    val currentUserProfileUiState: UserProfileUiState = UserProfileUiState(),
    val mutableUserProfileUiState: UserProfileUiState = UserProfileUiState(),
    val availableOfficesUiState: AvailableOfficesUiState = AvailableOfficesUiState(),
    val isChangesSaving: Boolean = false,
    val isChangesSaved: Boolean = false,
    val isChangesSavingError: Boolean = false,
) {
    val isLoading: Boolean
        get() = isChangesSaving ||
                currentUserProfileUiState.isLoading ||
                mutableUserProfileUiState.isLoading ||
                availableOfficesUiState.isLoading
}

data class AvailableOfficesUiState(
    val availableOffices: List<Office> = emptyList(),
    val isLoading: Boolean = false,
    val isErrorWhileLoading: Boolean = false
)

@HiltViewModel
class UserManageAccountViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val officeRepository: OfficeRepository,
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

    private fun updateIsChangesSaving(isChangesSaving: Boolean) {
        userManageAccountUiState = userManageAccountUiState.copy(
            isChangesSaving = isChangesSaving
        )
    }

    private fun updateIsChangesSavingError(isChangesSavingError: Boolean) {
        userManageAccountUiState = userManageAccountUiState.copy(
            isChangesSavingError = isChangesSavingError
        )
    }

    private fun updateAvailableOffices(availableOffices: List<Office>) {
        userManageAccountUiState = userManageAccountUiState.copy(
            availableOfficesUiState = userManageAccountUiState.availableOfficesUiState.copy(
                availableOffices = availableOffices
            )
        )
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

    private fun updateIsErrorWhileUserLoading(isErrorWhileLoading: Boolean) {
        userManageAccountUiState = userManageAccountUiState.copy(
            currentUserProfileUiState = userManageAccountUiState.currentUserProfileUiState.copy(
                isErrorWhileUserLoading = isErrorWhileLoading
            )
        )
    }

    private fun updateIsErrorWhileOfficesLoading(isErrorWhileLoading: Boolean) {
        userManageAccountUiState = userManageAccountUiState.copy(
            availableOfficesUiState = userManageAccountUiState.availableOfficesUiState.copy(
                isErrorWhileLoading = isErrorWhileLoading
            )
        )
    }

    private fun updateIsUserLoading(isLoading: Boolean) {
        userManageAccountUiState = userManageAccountUiState.copy(
            currentUserProfileUiState = userManageAccountUiState.currentUserProfileUiState.copy(
                isLoading = isLoading
            ),
            mutableUserProfileUiState = userManageAccountUiState.mutableUserProfileUiState.copy(
                isLoading = isLoading
            )
        )
    }

    private fun updateIsOfficesLoading(isLoading: Boolean) {
        userManageAccountUiState = userManageAccountUiState.copy(
            availableOfficesUiState = userManageAccountUiState.availableOfficesUiState.copy(
                isLoading = isLoading
            )
        )
    }

    fun save() {
        if (userInfoValid()) {
            viewModelScope.launch {
                updateIsChangesSaving(true)
                val userProfileUiState = userManageAccountUiState.mutableUserProfileUiState
                val photoUrlResult = uploadUserPhoto()
                if (photoUrlResult.isFailure) {
                    updateIsChangesSavingError(true)
                    updateIsChangesSaved(false)
                    updateIsChangesSaving(false)
                    return@launch
                }

                val photoUrl = photoUrlResult.getOrThrow()
                val userDto = userProfileUiState.toUserDto(photoUrl)
                userRepository.saveChanges(userDto).also { result ->
                    updateIsChangesSaving(false)
                    updateIsChangesSavingError(result.isFailure)
                    updateIsChangesSaved(result.isSuccess)
                }
            }
        }
    }

    private fun userInfoValid(): Boolean {
        val isNameValid = isNameValid()
        val isSurnameValid = isSurnameValid()
        val isJobValid = isJobValid()
        return isNameValid && isSurnameValid && isJobValid
    }

    private fun isNameValid(): Boolean {
        val name = userManageAccountUiState.mutableUserProfileUiState.name.value
        val nameValidationResult = validateUserInfo(name)
        return if (nameValidationResult is UserInfoValidationResult.Failure) {
            updateNameValidationError(nameValidationResult.error)
            false
        } else {
            updateNameValidationError(null)
            true
        }
    }

    private fun isSurnameValid(): Boolean {
        val surname = userManageAccountUiState.mutableUserProfileUiState.surname.value
        val surnameValidationResult = validateUserInfo(surname)
        return if (surnameValidationResult is UserInfoValidationResult.Failure) {
            updateSurnameValidationError(surnameValidationResult.error)
            false
        } else {
            updateSurnameValidationError(null)
            true
        }
    }

    private fun isJobValid(): Boolean {
        val job = userManageAccountUiState.mutableUserProfileUiState.job.value
        val jobValidationResult = validateUserInfo(job)
        return if (jobValidationResult is UserInfoValidationResult.Failure) {
            updateJobValidationError(jobValidationResult.error)
            false
        } else {
            updateJobValidationError(null)
            true
        }
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            updateIsUserLoading(true)
            authRepository.userInfo().also { result ->
                updateIsUserLoading(false)
                updateIsErrorWhileUserLoading(result.isFailure)
                if (result.isSuccess) {
                    val user = result.getOrThrow()
                    val userProfileUiState = user.toUserProfileUiState()
                    updateCurrentUserProfileUiState(userProfileUiState)
                    updateMutableUserProfileUiState(userProfileUiState)
                }
            }
        }
    }

    fun loadAvailableOffices() {
        viewModelScope.launch {
            updateIsOfficesLoading(true)
            officeRepository.availableOffices().also { result ->
                updateIsOfficesLoading(false)
                updateIsErrorWhileOfficesLoading(result.isFailure)
                if (result.isSuccess) {
                    val availableOffices = result.getOrThrow()
                    updateAvailableOffices(availableOffices)
                }
            }
        }
    }

    private suspend fun uploadUserPhoto(): Result<String?> =
        when (val pickedPhoto = userManageAccountUiState.mutableUserProfileUiState.photo) {
            is Uri -> {
                val photoUrlResult = imagesRepository.uploadImage(pickedPhoto)
                if (photoUrlResult.isSuccess) {
                    val photoUrl = photoUrlResult.getOrThrow()
                    Result.success(photoUrl)
                } else {
                    photoUrlResult
                }
            }

            is String -> Result.success(pickedPhoto)
            else -> Result.success(null)
        }
}

fun UserProfileUiState.toUserDto(photoUrl: String?) =
    UserDto(
        name = name.value,
        surname = surname.value,
        job = job.value,
        photo = photoUrl,
        officeId = currentOffice!!.id
    )