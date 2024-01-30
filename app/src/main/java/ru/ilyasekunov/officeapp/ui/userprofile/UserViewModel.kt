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
import ru.ilyasekunov.officeapp.data.officeList
import ru.ilyasekunov.officeapp.data.repository.user.UserRepository
import javax.inject.Inject

fun User.toUserInfoUiState(): UserInfoUiState =
    UserInfoUiState(
        email = email,
        password = password,
        name = name,
        surname = surname,
        job = job,
        photo = photo,
        office = office
    )

data class UserInfoUiState(
    val email: String = "",
    val password: String = "",
    val repeatedPassword: String = "",
    val name: String = "",
    val surname: String = "",
    val job: String = "",
    val photo: String? = null,
    val office: Office = officeList[0]
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    var user by mutableStateOf<User?>(null)
        private set
    var userInfoUiState by mutableStateOf(UserInfoUiState())
        private set
    var isUserFetching by mutableStateOf(false)
        private set
    val officeList = userRepository.findOfficeList()

    init {
        fetchUserInfo()
    }

    fun updateName(name: String) {
        userInfoUiState = userInfoUiState.copy(name = name)
    }

    fun updateSurname(surname: String) {
        userInfoUiState = userInfoUiState.copy(surname = surname)
    }

    fun updateJob(job: String) {
        userInfoUiState = userInfoUiState.copy(job = job)
    }

    fun updatePhotoUri(photo: String?) {
        userInfoUiState = userInfoUiState.copy(photo = photo)
    }

    fun updateOffice(office: Office) {
        userInfoUiState = userInfoUiState.copy(office = office)
    }

    fun save() {
        user = user?.copy(
            name = userInfoUiState.name,
            surname = userInfoUiState.surname,
            job = userInfoUiState.job,
            photo = userInfoUiState.photo,
            office = userInfoUiState.office
        )
    }

    fun logout() {

    }

    private fun fetchUserInfo() {
        viewModelScope.launch {
            isUserFetching = true
            user = userRepository.findUser()
            userInfoUiState = user!!.toUserInfoUiState()
            isUserFetching = false
        }
    }
}