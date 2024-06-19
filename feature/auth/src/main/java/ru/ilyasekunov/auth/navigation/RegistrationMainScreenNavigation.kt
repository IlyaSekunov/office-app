package ru.ilyasekunov.auth.navigation

import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import ru.ilyasekunov.auth.registration.RegistrationMainScreen
import ru.ilyasekunov.auth.registration.RegistrationViewModel
import ru.ilyasekunov.navigation.Screen
import ru.ilyasekunov.navigation.enterSlideLeft
import ru.ilyasekunov.navigation.exitSlideRight

data object RegistrationMainScreen : Screen("registration-main")

fun NavGraphBuilder.registrationMainScreen(
    navigateToLoginScreen: () -> Unit,
    navigateToRegistrationUserInfoScreen: () -> Unit
) {
    composable(
        route = RegistrationMainScreen.route,
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

                    val registrationUiState = registrationViewModel.registrationUiState
                    if (registrationUiState.credentialsValid) {
                        navigateToRegistrationUserInfoScreen()
                    }
                }
            },
            onNetworkErrorShown = registrationViewModel::networkErrorShown,
            navigateToLoginScreen = navigateToLoginScreen
        )
    }
}

fun NavController.navigateToRegistrationMainScreen(navOptions: NavOptions? = null) =
    navigate(RegistrationMainScreen.route, navOptions)