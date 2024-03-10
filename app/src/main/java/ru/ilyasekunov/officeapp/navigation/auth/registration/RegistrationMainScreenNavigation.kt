package ru.ilyasekunov.officeapp.navigation.auth.registration

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.navigation.Screen
import ru.ilyasekunov.officeapp.ui.animations.enterSlideLeft
import ru.ilyasekunov.officeapp.ui.animations.exitSlideRight
import ru.ilyasekunov.officeapp.ui.auth.registration.RegistrationMainScreen
import ru.ilyasekunov.officeapp.ui.auth.registration.RegistrationViewModel

fun NavGraphBuilder.registrationMainScreen(
    navigateToLogin: () -> Unit,
    navigateToRegistrationUserInfo: () -> Unit
) {
    composable(
        Screen.RegistrationMain.route,
        enterTransition = { enterSlideLeft() },
        exitTransition = { exitSlideRight() }
    ) {
        val registrationViewModel = hiltViewModel<RegistrationViewModel>()
        RegistrationMainScreen(
            registrationUiState = registrationViewModel.registrationUiState,
            onEmailValueChange = registrationViewModel::updateEmail,
            onPasswordValueChange = registrationViewModel::updatePassword,
            onRepeatPasswordValueChange = registrationViewModel::updateRepeatedPassword,
            onRegisterButtonClick = {
                if (registrationViewModel.credentialsValid()) {
                    navigateToRegistrationUserInfo()
                }
            },
            navigateToLoginScreen = navigateToLogin
        )
    }
}

fun NavController.navigateToRegistrationMainScreen(navOptions: NavOptions? = null) =
    navigate(Screen.RegistrationMain.route, navOptions)