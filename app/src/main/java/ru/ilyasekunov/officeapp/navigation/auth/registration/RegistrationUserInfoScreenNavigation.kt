package ru.ilyasekunov.officeapp.navigation.auth.registration

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.ui.auth.registration.RegistrationUserInfoScreen
import ru.ilyasekunov.officeapp.ui.user_profile.UserInfoViewModel

const val RegistrationUserInfoRoute = "registration-info"

fun NavGraphBuilder.registrationUserInfoScreen(
    navigateBack: () -> Unit
) {
    composable(RegistrationUserInfoRoute) {
        val userInfoViewModel = hiltViewModel<UserInfoViewModel>()
        val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let {
                userInfoViewModel.updatePhotoUri(it.toString())
            }
        }
        RegistrationUserInfoScreen(
            userInfoUiState = userInfoViewModel.userInfoUiState,
            navigateBack = navigateBack,
            onPhotoPickerClick = { galleryLauncher.launch("image/*") },
            onNameValueChange = userInfoViewModel::updateName,
            onSurnameValueChange = userInfoViewModel::updateSurname,
            onJobValueChange = userInfoViewModel::updateJob,
            onOfficeChange = userInfoViewModel::updateOffice,
            onSaveButtonClick = userInfoViewModel::save
        )
    }
}

fun NavController.navigateToRegistrationUserInfoScreen(navOptions: NavOptions? = null) =
    navigate(RegistrationUserInfoRoute, navOptions)