package ru.ilyasekunov.officeapp.ui.userprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.model.User
import ru.ilyasekunov.officeapp.data.repository.user.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    var user: User? = null
        private set

    init {
        fetchUserInfo()
    }

    fun updateUser(updatedUser: User) {
        viewModelScope.launch {
            user = updatedUser
            userRepository.saveChanges(updatedUser)
        }
    }

    fun logout() {

    }

    private fun fetchUserInfo() {
        viewModelScope.launch {
            user = userRepository.user()
        }
    }
}

infix fun User?.differFrom(userInfoUiState: UserInfoUiState): Boolean =
    when {
        this == null && userInfoUiState == UserInfoUiState.Empty -> false
        this == null || userInfoUiState == UserInfoUiState.Empty -> true
        else -> this.email != userInfoUiState.email ||
                this.password != userInfoUiState.password ||
                this.name != userInfoUiState.name ||
                this.surname != userInfoUiState.surname ||
                this.job != userInfoUiState.job ||
                this.office != userInfoUiState.office ||
                this.photo != userInfoUiState.photo
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