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

data class UserInfoUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val surname: String = "",
    val job: String = "",
    val photo: ByteArray? = null,
    val office: Office = officeList[0]
) {
    companion object {
        val Empty = UserInfoUiState()
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserInfoUiState

        if (email != other.email) return false
        if (password != other.password) return false
        if (name != other.name) return false
        if (surname != other.surname) return false
        if (job != other.job) return false
        if (photo != null) {
            if (other.photo == null) return false
            if (!photo.contentEquals(other.photo)) return false
        } else if (other.photo != null) return false
        return office == other.office
    }

    override fun hashCode(): Int {
        var result = email.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + surname.hashCode()
        result = 31 * result + job.hashCode()
        result = 31 * result + office.hashCode()
        return result
    }
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    var user by mutableStateOf<User?>(null)
        private set
    var userInfoUiState by mutableStateOf(UserInfoUiState.Empty)
        private set
    var isUserNewInfoUnsaved by mutableStateOf(false)
        private set

    init {
        fetchUserInfo()
    }

    fun updateName(name: String) {
        userInfoUiState = userInfoUiState.copy(name = name)
        isUserNewInfoUnsaved = user differFrom userInfoUiState
    }

    fun updateSurname(surname: String) {
        userInfoUiState = userInfoUiState.copy(surname = surname)
        isUserNewInfoUnsaved = user differFrom userInfoUiState
    }

    fun updateJob(job: String) {
        userInfoUiState = userInfoUiState.copy(job = job)
        isUserNewInfoUnsaved = user differFrom userInfoUiState
    }

    fun updatePhoto(photo: ByteArray?) {
        userInfoUiState = userInfoUiState.copy(photo = photo)
        isUserNewInfoUnsaved = user differFrom userInfoUiState
    }

    fun updateOffice(office: Office) {
        userInfoUiState = userInfoUiState.copy(office = office)
        isUserNewInfoUnsaved = user differFrom userInfoUiState
    }

    fun restoreUserInfoUiChanges() {
        userInfoUiState = user?.toUserInfoUiState() ?: UserInfoUiState()
    }

    fun save() {
        user = user?.copy(
            email = userInfoUiState.email,
            password = userInfoUiState.password,
            name = userInfoUiState.name,
            surname = userInfoUiState.surname,
            job = userInfoUiState.job,
            photo = userInfoUiState.photo,
            office = userInfoUiState.office
        )
        isUserNewInfoUnsaved = false
    }

    fun logout() {

    }

    private fun fetchUserInfo() {
        viewModelScope.launch {
            user = userRepository.findUser()
            user?.let {
                userInfoUiState = it.toUserInfoUiState()
            }
        }
    }
}

private infix fun User?.differFrom(userInfoUiState: UserInfoUiState): Boolean =
    when {
        this == null && userInfoUiState == UserInfoUiState.Empty -> false
        this == null || userInfoUiState == UserInfoUiState.Empty -> true
        else -> this.email != userInfoUiState.email ||
                this.password != userInfoUiState.password ||
                this.name != userInfoUiState.name ||
                this.surname != userInfoUiState.surname ||
                this.job != userInfoUiState.job ||
                this.office != userInfoUiState.office ||
                !this.photo.contentEquals(userInfoUiState.photo)
    }

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