package ru.ilyasekunov.officeapp.navigation.auth.registration

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.navigation.Screen
import ru.ilyasekunov.officeapp.ui.auth.registration.RegistrationUserInfoScreen
import ru.ilyasekunov.officeapp.ui.userprofile.UserInfoViewModel

fun NavGraphBuilder.registrationUserInfoScreen(
    navigateBack: () -> Unit,
    navigateToMainGraph: () -> Unit
) {
    composable(Screen.RegistrationUserInfo.route) {
        val userInfoViewModel = hiltViewModel<UserInfoViewModel>()
        val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let {
                userInfoViewModel.updatePhotoUri(it.toString())
            }
        }
        RegistrationUserInfoScreen(
            userInfoUiState = userInfoViewModel.userInfoUiState,
            officeList = userInfoViewModel.officeList,
            navigateBack = navigateBack,
            onPhotoPickerClick = { galleryLauncher.launch("image/*") },
            onNameValueChange = userInfoViewModel::updateName,
            onSurnameValueChange = userInfoViewModel::updateSurname,
            onJobValueChange = userInfoViewModel::updateJob,
            onOfficeChange = userInfoViewModel::updateOffice,
            onSaveButtonClick = {
                userInfoViewModel.save()
                navigateToMainGraph()
            }
        )
    }
}

fun NavController.navigateToRegistrationUserInfoScreen(navOptions: NavOptions? = null) =
    navigate(Screen.RegistrationUserInfo.route, navOptions)