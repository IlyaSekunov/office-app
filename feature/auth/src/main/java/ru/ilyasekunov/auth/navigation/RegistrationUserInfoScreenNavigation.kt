package ru.ilyasekunov.auth.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.auth.registration.RegistrationUserInfoScreen
import ru.ilyasekunov.auth.registration.RegistrationViewModel
import ru.ilyasekunov.navigation.Screen
import ru.ilyasekunov.navigation.enterSlideLeft
import ru.ilyasekunov.navigation.exitSlideRight

data object RegistrationUserInfoScreen : Screen("registration-user-info")

fun NavGraphBuilder.registrationUserInfoScreen(
    viewModelStoreOwnerProvider: () -> ViewModelStoreOwner,
    navigateBack: () -> Unit,
    navigateToMainGraph: () -> Unit
) {
    composable(
        route = RegistrationUserInfoScreen.route,
        enterTransition = { enterSlideLeft() },
        exitTransition = { exitSlideRight() }
    ) { backStackEntry ->
        val viewModelStoreOwner = remember(backStackEntry) { viewModelStoreOwnerProvider() }
        val registrationViewModel = hiltViewModel<RegistrationViewModel>(viewModelStoreOwner)

        RegistrationUserInfoScreen(
            registrationUiState = registrationViewModel.registrationUiState,
            availableOfficesUiState = registrationViewModel.availableOfficesUiState,
            onAttachImage = registrationViewModel::updatePhoto,
            onNameValueChange = registrationViewModel::updateName,
            onSurnameValueChange = registrationViewModel::updateSurname,
            onJobValueChange = registrationViewModel::updateJob,
            onOfficeChange = registrationViewModel::updateOffice,
            onSaveButtonClick = registrationViewModel::register,
            onNetworkErrorShown = registrationViewModel::networkErrorShown,
            navigateBack = navigateBack,
            onRetryButtonClick = registrationViewModel::loadAvailableOffices,
            navigateToHomeScreen = navigateToMainGraph
        )
    }
}

fun NavController.navigateToRegistrationUserInfoScreen(navOptions: NavOptions? = null) =
    navigate(RegistrationUserInfoScreen.route, navOptions)