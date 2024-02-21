package ru.ilyasekunov.officeapp.ui.auth.registration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.dto.UserDto
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.repository.auth.AuthRepository
import ru.ilyasekunov.officeapp.data.repository.user.UserRepository
import ru.ilyasekunov.officeapp.ui.userprofile.UserProfileUiState
import javax.inject.Inject

data class RegistrationUiState(
    val email: String = "",
    val password: String = "",
    val repeatedPassword: String = "",
    val userInfoUiState: UserProfileUiState = UserProfileUiState(),
    val availableOffices: List<Office> = emptyList(),
    val isLoading: Boolean = false,
    val isRegistrationSuccess: Boolean = false
)

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    var registrationUiState by mutableStateOf(RegistrationUiState())
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
            userInfoUiState = registrationUiState.userInfoUiState.copy(name = name)
        )
    }

    fun updateSurname(surname: String) {
        registrationUiState = registrationUiState.copy(
            userInfoUiState = registrationUiState.userInfoUiState.copy(surname = surname)
        )
    }

    fun updateJob(job: String) {
        registrationUiState = registrationUiState.copy(
            userInfoUiState = registrationUiState.userInfoUiState.copy(job = job)
        )
    }

    fun updatePhoto(photo: ByteArray?) {
        registrationUiState = registrationUiState.copy(
            userInfoUiState = registrationUiState.userInfoUiState.copy(photo = photo)
        )
    }

    fun updateOffice(office: Office) {
        registrationUiState = registrationUiState.copy(
            userInfoUiState = registrationUiState.userInfoUiState.copy(currentOffice = office)
        )
    }

    fun register() {
        viewModelScope.launch {
            updateIsLoading(true)
            //authRepository.register(registrationUiState.toRegistrationForm())
            updateIsLoading(false)
            updateIsRegistrationSuccess(true)
        }
    }

    private fun updateAvailableOffices(availableOffices: List<Office>) {
        registrationUiState = registrationUiState.copy(availableOffices = availableOffices)
    }

    private fun updateIsLoading(isLoading: Boolean) {
        registrationUiState = registrationUiState.copy(isLoading = isLoading)
    }

    private fun updateIsRegistrationSuccess(isRegistrationSuccess: Boolean) {
        registrationUiState = registrationUiState.copy(isRegistrationSuccess = isRegistrationSuccess)
    }

    private fun loadAvailableOffices() {
        viewModelScope.launch {
            updateIsLoading(true)
            updateAvailableOffices(userRepository.availableOffices())
            updateOffice(registrationUiState.availableOffices[0])
            updateIsLoading(false)
        }
    }
}

fun RegistrationUiState.toRegistrationForm(): RegistrationForm =
    RegistrationForm(
        email = email,
        password = password,
        userInfo = UserDto(
            name = userInfoUiState.name,
            surname = userInfoUiState.surname,
            job = userInfoUiState.job,
            photo = userInfoUiState.photo,
            officeId = userInfoUiState.currentOffice!!.id
        )
    )