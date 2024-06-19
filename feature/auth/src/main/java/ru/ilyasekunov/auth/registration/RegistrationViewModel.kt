package ru.ilyasekunov.auth.registration

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.auth.login.EmailUiState
import ru.ilyasekunov.auth.login.PasswordUiState
import ru.ilyasekunov.auth.repository.AuthRepository
import ru.ilyasekunov.dto.RegistrationForm
import ru.ilyasekunov.dto.UserDto
import ru.ilyasekunov.images.repository.ImagesRepository
import ru.ilyasekunov.model.Office
import ru.ilyasekunov.office.repository.OfficeRepository
import ru.ilyasekunov.validation.EmailValidationError
import ru.ilyasekunov.validation.EmailValidationResult
import ru.ilyasekunov.validation.PasswordValidationError
import ru.ilyasekunov.validation.PasswordValidationResult
import ru.ilyasekunov.validation.UserInfoValidationError
import ru.ilyasekunov.validation.UserInfoValidationResult
import ru.ilyasekunov.validation.validateEmail
import ru.ilyasekunov.validation.validatePassword
import ru.ilyasekunov.validation.validateUserInfo
import javax.inject.Inject

@Immutable
data class RegistrationUiState(
    val emailUiState: EmailUiState = EmailUiState(),
    val passwordUiState: PasswordUiState = PasswordUiState(),
    val repeatedPasswordUiState: PasswordUiState = PasswordUiState(),
    val userInfoRegistrationUiState: UserInfoRegistrationUiState = UserInfoRegistrationUiState(),
    val isLoading: Boolean = false,
    val isRegistrationSuccess: Boolean = false,
    val isNetworkError: Boolean = false,
    val passwordsDiffer: Boolean = false
) {
    val credentialsValid: Boolean
        get() = emailUiState.error == null &&
                passwordUiState.error == null &&
                repeatedPasswordUiState.error == null &&
                !passwordsDiffer
}

@Immutable
data class AvailableOfficesUiState(
    val availableOffices: List<Office> = emptyList(),
    val isLoading: Boolean = false,
    val isErrorWhileLoading: Boolean = true
)

@Immutable
data class UserInfoRegistrationUiState(
    val name: UserInfoFieldUiState = UserInfoFieldUiState(),
    val surname: UserInfoFieldUiState = UserInfoFieldUiState(),
    val job: UserInfoFieldUiState = UserInfoFieldUiState(),
    val photo: Uri? = null,
    val currentOffice: Office? = null,
    val isLoading: Boolean = false
)

@Immutable
data class UserInfoFieldUiState(
    val value: String = "",
    val error: UserInfoValidationError? = null
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
        registrationUiState = registrationUiState.copy(
            emailUiState = registrationUiState.emailUiState.copy(email = email)
        )
    }

    fun updatePassword(password: String) {
        registrationUiState = registrationUiState.copy(
            passwordUiState = registrationUiState.passwordUiState.copy(password = password)
        )
    }

    fun updateRepeatedPassword(repeatedPassword: String) {
        registrationUiState = registrationUiState.copy(
            repeatedPasswordUiState = registrationUiState.repeatedPasswordUiState.copy(
                password = repeatedPassword
            )
        )
    }

    fun updateName(name: String) {
        val userInfoRegistrationUiState = registrationUiState.userInfoRegistrationUiState
        registrationUiState = registrationUiState.copy(
            userInfoRegistrationUiState = userInfoRegistrationUiState.copy(
                name = userInfoRegistrationUiState.name.copy(value = name)
            )
        )
    }

    fun updateSurname(surname: String) {
        val userInfoRegistrationUiState = registrationUiState.userInfoRegistrationUiState
        registrationUiState = registrationUiState.copy(
            userInfoRegistrationUiState = userInfoRegistrationUiState.copy(
                surname = userInfoRegistrationUiState.surname.copy(value = surname)
            )
        )
    }

    fun updateJob(job: String) {
        val userInfoRegistrationUiState = registrationUiState.userInfoRegistrationUiState
        registrationUiState = registrationUiState.copy(
            userInfoRegistrationUiState = userInfoRegistrationUiState.copy(
                job = userInfoRegistrationUiState.job.copy(value = job)
            )
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

    fun networkErrorShown() {
        registrationUiState = registrationUiState.copy(isNetworkError = false)
    }

    fun register() {
        viewModelScope.launch {
            if (registrationUiState.credentialsValid && userInfoValid()) {
                registrationUiState = registrationUiState.copy(isLoading = true)
                val photoUrlResult = uploadUserPhoto()
                if (photoUrlResult.isFailure) {
                    registrationUiState = registrationUiState.copy(
                        isLoading = false,
                        isNetworkError = false,
                        isRegistrationSuccess = false
                    )
                    return@launch
                }

                val photoUrl = photoUrlResult.getOrThrow()
                register(photoUrl)
            }
        }
    }

    private suspend fun register(photoUrl: String?) {
        val registrationForm = registrationUiState.toRegistrationForm(photoUrl)
        authRepository.register(registrationForm).also { result ->
            registrationUiState = registrationUiState.copy(
                isLoading = false,
                isNetworkError = result.isFailure,
                isRegistrationSuccess = result.isSuccess
            )
        }
    }

    private fun updateAvailableOffices(availableOffices: List<Office>) {
        availableOfficesUiState = availableOfficesUiState.copy(availableOffices = availableOffices)
    }

    private fun updateIsNetworkError(isNetworkError: Boolean) {
        registrationUiState = registrationUiState.copy(isNetworkError = isNetworkError)
    }

    private fun updateEmailValidationError(error: EmailValidationError?) {
        registrationUiState = registrationUiState.copy(
            emailUiState = registrationUiState.emailUiState.copy(error = error)
        )
    }

    private fun updatePasswordValidationError(error: PasswordValidationError?) {
        registrationUiState = registrationUiState.copy(
            passwordUiState = registrationUiState.passwordUiState.copy(error = error)
        )
    }

    private fun updateRepeatedPasswordValidationError(error: PasswordValidationError?) {
        registrationUiState = registrationUiState.copy(
            repeatedPasswordUiState = registrationUiState.repeatedPasswordUiState.copy(
                error = error
            )
        )
    }

    private fun updateNameValidationError(error: UserInfoValidationError?) {
        val userInfoRegistrationUiState = registrationUiState.userInfoRegistrationUiState
        registrationUiState = registrationUiState.copy(
            userInfoRegistrationUiState = userInfoRegistrationUiState.copy(
                name = userInfoRegistrationUiState.name.copy(error = error)
            )
        )
    }

    private fun updateSurnameValidationError(error: UserInfoValidationError?) {
        val userInfoRegistrationUiState = registrationUiState.userInfoRegistrationUiState
        registrationUiState = registrationUiState.copy(
            userInfoRegistrationUiState = userInfoRegistrationUiState.copy(
                surname = userInfoRegistrationUiState.surname.copy(error = error)
            )
        )
    }

    private fun updateJobValidationError(error: UserInfoValidationError?) {
        val userInfoRegistrationUiState = registrationUiState.userInfoRegistrationUiState
        registrationUiState = registrationUiState.copy(
            userInfoRegistrationUiState = userInfoRegistrationUiState.copy(
                job = userInfoRegistrationUiState.job.copy(error = error)
            )
        )
    }

    private fun updatePasswordsDiffer(passwordsDiffer: Boolean) {
        registrationUiState = registrationUiState.copy(passwordsDiffer = passwordsDiffer)
    }

    fun loadAvailableOffices() {
        viewModelScope.launch {
            availableOfficesUiState = availableOfficesUiState.copy(isLoading = true)
            officeRepository.availableOffices().also { result ->
                availableOfficesUiState = availableOfficesUiState.copy(
                    isLoading = false,
                    isErrorWhileLoading = result.isFailure
                )
                if (result.isSuccess) {
                    val availableOffices = result.getOrThrow()
                    updateAvailableOffices(availableOffices)
                    updateOffice(availableOffices[0])
                }
            }
        }
    }

    suspend fun validateCredentials() {
        registrationUiState = registrationUiState.copy(isLoading = true)
        validateEmailPattern()
        validatePasswordPattern()
        validateRepeatedPasswordPattern()
        val password = registrationUiState.passwordUiState.password
        val repeatedPassword = registrationUiState.repeatedPasswordUiState.password
        updatePasswordsDiffer(password != repeatedPassword)
        if (registrationUiState.credentialsValid) {
            checkEmailAvailability()
        }
        registrationUiState = registrationUiState.copy(isLoading = false)
    }

    private fun validateEmailPattern() {
        val email = registrationUiState.emailUiState.email
        val emailValidationResult = validateEmail(email)
        if (emailValidationResult is EmailValidationResult.Failure) {
            updateEmailValidationError(emailValidationResult.error)
        } else {
            updateEmailValidationError(null)
        }
    }

    private suspend fun checkEmailAvailability() {
        val email = registrationUiState.emailUiState.email
        authRepository.isEmailValid(email).also { result ->
            updateIsNetworkError(result.isFailure)
            if (result.isSuccess) {
                val isEmailAvailable = result.getOrThrow()
                val emailValidationError = if (isEmailAvailable) {
                    null
                } else {
                    EmailValidationError.UNAVAILABLE
                }
                updateEmailValidationError(emailValidationError)
            }
        }
    }

    private fun validatePasswordPattern() {
        val password = registrationUiState.passwordUiState.password
        val passwordValidationResult = validatePassword(password)
        if (passwordValidationResult is PasswordValidationResult.Failure) {
            updatePasswordValidationError(passwordValidationResult.error)
        } else {
            updatePasswordValidationError(null)
        }
    }

    private fun validateRepeatedPasswordPattern() {
        val repeatedPassword = registrationUiState.repeatedPasswordUiState.password
        val repeatedPasswordValidationResult = validatePassword(repeatedPassword)
        if (repeatedPasswordValidationResult is PasswordValidationResult.Failure) {
            updateRepeatedPasswordValidationError(repeatedPasswordValidationResult.error)
        } else {
            updateRepeatedPasswordValidationError(null)
        }
    }

    private fun userInfoValid(): Boolean {
        val isNameValid = isNameValid()
        val isSurnameValid = isSurnameValid()
        val isJobValid = isJobValid()
        return isNameValid && isSurnameValid && isJobValid
    }

    private fun isNameValid(): Boolean {
        val name = registrationUiState.userInfoRegistrationUiState.name.value
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
        val surname = registrationUiState.userInfoRegistrationUiState.name.value
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
        val job = registrationUiState.userInfoRegistrationUiState.name.value
        val jobValidationResult = validateUserInfo(job)
        return if (jobValidationResult is UserInfoValidationResult.Failure) {
            updateJobValidationError(jobValidationResult.error)
            false
        } else {
            updateJobValidationError(null)
            true
        }
    }

    private suspend fun uploadUserPhoto(): Result<String?> {
        val pickedPhotoUri = registrationUiState.userInfoRegistrationUiState.photo
        return pickedPhotoUri?.let {
            val photoUrlResult = imagesRepository.uploadImage(it)
            if (photoUrlResult.isSuccess) {
                val photoUrl = photoUrlResult.getOrThrow()
                Result.success(photoUrl)
            } else {
                photoUrlResult
            }
        } ?: Result.success(null)
    }
}

private fun RegistrationUiState.toRegistrationForm(photoUrl: String?): RegistrationForm {
    val userInfo = userInfoRegistrationUiState
    return RegistrationForm(
        email = emailUiState.email,
        password = passwordUiState.password,
        userInfo = UserDto(
            name = userInfo.name.value,
            surname = userInfo.surname.value,
            job = userInfo.job.value,
            officeId = userInfo.currentOffice!!.id,
            photo = photoUrl
        )
    )
}