package ru.ilyasekunov.officeapp.navigation.auth.registration

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.data.officeList
import ru.ilyasekunov.officeapp.navigation.Screen
import ru.ilyasekunov.officeapp.ui.auth.registration.RegistrationUserInfoScreen
import ru.ilyasekunov.officeapp.ui.auth.registration.RegistrationViewModel

fun NavGraphBuilder.registrationUserInfoScreen(
    viewModelStoreOwnerProvider: () -> ViewModelStoreOwner,
    navigateBack: () -> Unit,
    navigateToMainGraph: () -> Unit
) {
    composable(Screen.RegistrationUserInfo.route) { backStackEntry ->
        val viewModelStoreOwner = remember(backStackEntry) {
            viewModelStoreOwnerProvider()
        }
        val registrationViewModel =
            hiltViewModel<RegistrationViewModel>(viewModelStoreOwner)
        val galleryLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
                registrationViewModel.updatePhoto(it.toString())
            }
        RegistrationUserInfoScreen(
            userInfoUiState = registrationViewModel.registrationUiState.userInfoUiState,
            officeList = officeList,
            navigateBack = navigateBack,
            onPhotoPickerClick = { galleryLauncher.launch("image/*") },
            onNameValueChange = registrationViewModel::updateName,
            onSurnameValueChange = registrationViewModel::updateSurname,
            onJobValueChange = registrationViewModel::updateJob,
            onOfficeChange = registrationViewModel::updateOffice,
            onSaveButtonClick = {
                registrationViewModel.register()
                navigateToMainGraph()
            }
        )
    }
}

fun NavController.navigateToRegistrationUserInfoScreen(navOptions: NavOptions? = null) =
    navigate(Screen.RegistrationUserInfo.route, navOptions)