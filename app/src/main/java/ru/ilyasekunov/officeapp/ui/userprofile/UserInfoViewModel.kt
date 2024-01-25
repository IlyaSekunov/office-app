package ru.ilyasekunov.officeapp.ui.userprofile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.ilyasekunov.officeapp.preview.officeListPreview
import ru.ilyasekunov.officeapp.ui.components.Office
import javax.inject.Inject

data class UserInfoUiState(
    val name: String = "",
    val surname: String = "",
    val job: String = "",
    val photo: String? = null,
    val office: Office = Office(0, "", "")
)

@HiltViewModel
class UserInfoViewModel @Inject constructor() : ViewModel() {
    var userInfoUiState by mutableStateOf(UserInfoUiState())
        private set

    val officeList = officeListPreview

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

    }
}