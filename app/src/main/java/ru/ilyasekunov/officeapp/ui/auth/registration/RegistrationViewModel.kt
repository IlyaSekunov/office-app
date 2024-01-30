package ru.ilyasekunov.officeapp.ui.auth.registration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.dto.UserInfoForm
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.repository.user.UserRepository
import ru.ilyasekunov.officeapp.ui.userprofile.UserInfoUiState
import javax.inject.Inject

data class RegistrationUiState(
    val email: String = "",
    val password: String = "",
    val repeatedPassword: String = "",
    val userInfoUiState: UserInfoUiState = UserInfoUiState()
)

fun RegistrationUiState.toRegistrationForm(): RegistrationForm =
    RegistrationForm(
        email = email,
        password = password,
        userInfo = UserInfoForm(
            name = userInfoUiState.name,
            surname = userInfoUiState.surname,
            job = userInfoUiState.job,
            photo = userInfoUiState.photo,
            officeId = userInfoUiState.office.id
        )
    )

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    var registrationUiState by mutableStateOf(RegistrationUiState())
        private set
    val officeList = userRepository.findOfficeList()

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

    fun updatePhoto(photo: String?) {
        registrationUiState = registrationUiState.copy(
            userInfoUiState = registrationUiState.userInfoUiState.copy(photo = photo)
        )
    }

    fun updateOffice(office: Office) {
        registrationUiState = registrationUiState.copy(
            userInfoUiState = registrationUiState.userInfoUiState.copy(office = office)
        )
    }

    fun register() {
        userRepository.register(registrationUiState.toRegistrationForm())
    }
}