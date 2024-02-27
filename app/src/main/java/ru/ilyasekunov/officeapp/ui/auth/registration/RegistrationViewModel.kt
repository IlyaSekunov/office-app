package ru.ilyasekunov.officeapp.ui.auth.registration

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.ResponseResult
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.repository.auth.AuthRepository
import ru.ilyasekunov.officeapp.data.repository.images.ImagesRepository
import ru.ilyasekunov.officeapp.data.repository.office.OfficeRepository
import javax.inject.Inject

data class RegistrationUiState(
    val email: String = "",
    val password: String = "",
    val repeatedPassword: String = "",
    val userInfoRegistrationUiState: UserInfoRegistrationUiState = UserInfoRegistrationUiState(),
    val isLoading: Boolean = false,
    val isRegistrationSuccess: Boolean = false,
    val errorMessage: String? = null
)

data class AvailableOfficesUiState(
    val availableOffices: List<Office> = emptyList(),
    val isLoading: Boolean = false,
    val isErrorWhileLoading: Boolean = true,
)

data class UserInfoRegistrationUiState(
    val name: String = "",
    val surname: String = "",
    val job: String = "",
    val photo: Uri? = null,
    val currentOffice: Office? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val imagesRepository: ImagesRepository,
    private val officeRepository: OfficeRepository
) : ViewModel() {
    var registrationUiState by mutableStateOf(RegistrationUiState())
        private set
    var availableOfficesUiState by mutableStateOf(AvailableOfficesUiState())
        private set

    init {
        loadAvailableOffices()
    }

    fun updateEmail(email: String) {
        registrationUiState = registrationUiState.copy(email = email)
    }

    fun updatePassword(password: String) {
        registrationUiState = registrationUiState.copy(password = password)
    }

    fun updateRepeatedPassword(repeatedPassword: String) {
        registrationUiState = registrationUiState.copy(repeatedPassword = repeatedPassword)
    }

    fun updateName(name: String) {
        registrationUiState = registrationUiState.copy(
            userInfoRegistrationUiState = registrationUiState.userInfoRegistrationUiState.copy(name = name)
        )
    }

    fun updateSurname(surname: String) {
        registrationUiState = registrationUiState.copy(
            userInfoRegistrationUiState = registrationUiState.userInfoRegistrationUiState.copy(
                surname = surname
            )
        )
    }

    fun updateJob(job: String) {
        registrationUiState = registrationUiState.copy(
            userInfoRegistrationUiState = registrationUiState.userInfoRegistrationUiState.copy(job = job)
        )
    }

    fun updatePhoto(photo: Uri?) {
        registrationUiState = registrationUiState.copy(
            userInfoRegistrationUiState = registrationUiState.userInfoRegistrationUiState.copy(photo = photo)
        )
    }

    fun updateOffice(office: Office) {
        registrationUiState = registrationUiState.copy(
            userInfoRegistrationUiState = registrationUiState.userInfoRegistrationUiState.copy(
                currentOffice = office
            )
        )
    }

    fun register() {
        viewModelScope.launch {
            updateIsLoading(true)
            /*val uploadedUserPhoto = uploadUserPhoto()
            if (registrationUiState.userInfoRegistrationUiState.errorMessage != null) {
                updateIsLoading(false)
                return@launch
            }

            val userInfo = registrationUiState.userInfoRegistrationUiState
            val registrationForm = RegistrationForm(
                email = registrationUiState.email,
                password = registrationUiState.password,
                userInfo = UserDto(
                    name = userInfo.name,
                    surname = userInfo.surname,
                    job = userInfo.job,
                    officeId = userInfo.currentOffice!!.id,
                    photo = uploadedUserPhoto
                )
            )
            authRepository.register(registrationForm)*/
            updateIsLoading(false)
            updateIsRegistrationSuccess(true)
        }
    }

    private fun updateAvailableOffices(availableOffices: List<Office>) {
        availableOfficesUiState = availableOfficesUiState.copy(availableOffices = availableOffices)
    }

    private fun updateIsLoading(isLoading: Boolean) {
        registrationUiState = registrationUiState.copy(isLoading = isLoading)
    }

    private fun updateAvailableOfficesIsLoading(isLoading: Boolean) {
        availableOfficesUiState = availableOfficesUiState.copy(isLoading = isLoading)
    }

    private fun updateIsRegistrationSuccess(isRegistrationSuccess: Boolean) {
        registrationUiState =
            registrationUiState.copy(isRegistrationSuccess = isRegistrationSuccess)
    }

    fun loadAvailableOffices() {
        viewModelScope.launch {
            updateAvailableOfficesIsLoading(true)
            when (val availableOfficesResponse = officeRepository.availableOffices()) {
                is ResponseResult.Success -> {
                    updateAvailableOfficeIsError(false)
                    updateAvailableOffices(availableOfficesResponse.value)
                    updateOffice(availableOfficesUiState.availableOffices[0])
                }

                is ResponseResult.Failure -> {
                    updateAvailableOfficeIsError(true)
                }
            }
            updateAvailableOfficesIsLoading(false)
        }
    }

    private fun updateUserInfoErrorMessage(errorMessage: String?) {
        registrationUiState = registrationUiState.copy(
            userInfoRegistrationUiState = registrationUiState.userInfoRegistrationUiState.copy(
                errorMessage = errorMessage
            )
        )
    }

    private fun updateRegistrationErrorMessage(errorMessage: String?) {
        registrationUiState = registrationUiState.copy(errorMessage = errorMessage)
    }

    private fun updateAvailableOfficeIsError(isErrorWhileLoading: Boolean) {
        availableOfficesUiState = availableOfficesUiState.copy(isErrorWhileLoading = isErrorWhileLoading)
    }

    private suspend fun uploadUserPhoto(): String? {
        val pickedPhoto = registrationUiState.userInfoRegistrationUiState.photo
        return pickedPhoto?.let {
            when (val response = imagesRepository.uploadImage(it)) {
                is ResponseResult.Success -> {
                    response.value
                }

                is ResponseResult.Failure -> {
                    updateUserInfoErrorMessage(response.message)
                    return null
                }
            }
        }
    }
}