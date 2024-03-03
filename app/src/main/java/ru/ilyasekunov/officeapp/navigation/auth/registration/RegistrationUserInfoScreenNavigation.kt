package ru.ilyasekunov.officeapp.navigation.auth.registration

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.navigation.Screen
import ru.ilyasekunov.officeapp.ui.animations.enterSlideLeft
import ru.ilyasekunov.officeapp.ui.animations.exitSlideRight
import ru.ilyasekunov.officeapp.ui.auth.registration.RegistrationUserInfoScreen
import ru.ilyasekunov.officeapp.ui.auth.registration.RegistrationViewModel
import ru.ilyasekunov.officeapp.ui.util.rememberSingleImagePicker

fun NavGraphBuilder.registrationUserInfoScreen(
    viewModelStoreOwnerProvider: () -> ViewModelStoreOwner,
    navigateBack: () -> Unit,
    navigateToMainGraph: () -> Unit
) {
    composable(
        Screen.RegistrationUserInfo.route,
        enterTransition = { enterSlideLeft() },
        exitTransition = { exitSlideRight() }
    ) { backStackEntry ->
        val viewModelStoreOwner = remember(backStackEntry) { viewModelStoreOwnerProvider() }
        val registrationViewModel = hiltViewModel<RegistrationViewModel>(viewModelStoreOwner)
        val singleImagePicker = rememberSingleImagePicker(
            onUriPicked = registrationViewModel::updatePhoto
        )
        RegistrationUserInfoScreen(
            registrationUiState = registrationViewModel.registrationUiState,
            availableOfficesUiState = registrationViewModel.availableOfficesUiState,
            onPhotoPickerClick = singleImagePicker::launch,
            onNameValueChange = registrationViewModel::updateName,
            onSurnameValueChange = registrationViewModel::updateSurname,
            onJobValueChange = registrationViewModel::updateJob,
            onOfficeChange = registrationViewModel::updateOffice,
            onSaveButtonClick = registrationViewModel::register,
            navigateBack = navigateBack,
            onRetryButtonClick = registrationViewModel::loadAvailableOffices,
            navigateToHomeScreen = navigateToMainGraph
        )
    }
}

fun NavController.navigateToRegistrationUserInfoScreen(navOptions: NavOptions? = null) =
    navigate(Screen.RegistrationUserInfo.route, navOptions)