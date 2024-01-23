package ru.ilyasekunov.officeapp.navigation.auth.registration

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.ui.auth.registration.RegistrationMainScreen
import ru.ilyasekunov.officeapp.ui.auth.registration.RegistrationViewModel

const val RegistrationMainRoute = "registration-main"

fun NavGraphBuilder.registrationMainScreen(
    navigateToLogin: () -> Unit,
    navigateToRegistrationUserInfo: () -> Unit
) {
    composable(RegistrationMainRoute) {
        val registrationViewModel = hiltViewModel<RegistrationViewModel>()
        RegistrationMainScreen(
            registrationUiState = registrationViewModel.registrationUiState,
            onEmailValueChange = registrationViewModel::updateEmail,
            onPasswordValueChange = registrationViewModel::updatePassword,
            onRepeatPasswordValueChange = registrationViewModel::updateRepeatedPassword,
            onRegisterButtonClick = {
                registrationViewModel.register()
                navigateToRegistrationUserInfo()
            },
            navigateToLogin = navigateToLogin
        )
    }
}

fun NavController.navigateToRegistrationMainScreen(navOptions: NavOptions? = null) =
    navigate(RegistrationMainRoute, navOptions)