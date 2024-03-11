package ru.ilyasekunov.officeapp.navigation.auth.registration

import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.navigation.Screen
import ru.ilyasekunov.officeapp.ui.animations.enterSlideLeft
import ru.ilyasekunov.officeapp.ui.animations.exitSlideRight
import ru.ilyasekunov.officeapp.ui.auth.registration.RegistrationMainScreen
import ru.ilyasekunov.officeapp.ui.auth.registration.RegistrationViewModel

fun NavGraphBuilder.registrationMainScreen(
    navigateToLoginScreen: () -> Unit,
    navigateToRegistrationUserInfoScreen: () -> Unit
) {
    composable(
        Screen.RegistrationMain.route,
        enterTransition = { enterSlideLeft() },
        exitTransition = { exitSlideRight() }
    ) {
        val registrationViewModel = hiltViewModel<RegistrationViewModel>()
        val coroutineScope = rememberCoroutineScope()
        RegistrationMainScreen(
            registrationUiState = registrationViewModel.registrationUiState,
            onEmailValueChange = registrationViewModel::updateEmail,
            onPasswordValueChange = registrationViewModel::updatePassword,
            onRepeatPasswordValueChange = registrationViewModel::updateRepeatedPassword,
            onRegisterButtonClick = {
                coroutineScope.launch {
                    registrationViewModel.validateCredentials()
                    if (registrationViewModel.registrationUiState.isCredentialsValid) {
                        navigateToRegistrationUserInfoScreen()
                    }
                }
            },
            navigateToLoginScreen = navigateToLoginScreen,
            navigateToRegistrationUserInfoScreen = navigateToRegistrationUserInfoScreen
        )
    }
}

fun NavController.navigateToRegistrationMainScreen(navOptions: NavOptions? = null) =
    navigate(Screen.RegistrationMain.route, navOptions)