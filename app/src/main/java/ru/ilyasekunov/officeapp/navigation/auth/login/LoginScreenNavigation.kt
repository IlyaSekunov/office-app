package ru.ilyasekunov.officeapp.navigation.auth.login

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.navigation.Screen
import ru.ilyasekunov.officeapp.ui.auth.login.LoginScreen
import ru.ilyasekunov.officeapp.ui.auth.login.LoginViewModel

fun NavGraphBuilder.loginScreen(
    navigateToRegistration: () -> Unit,
    navigateToMainGraph: () -> Unit
) {
    composable(Screen.Login.route) {
        val loginViewModel = hiltViewModel<LoginViewModel>()
        LoginScreen(
            loginUiState = loginViewModel.loginUiState,
            onEmailValueChange = loginViewModel::updateEmail,
            onPasswordValueChange = loginViewModel::updatePassword,
            navigateToRegistrationMainScreen = navigateToRegistration,
            onLoginButtonClick = {
                loginViewModel.login()
                navigateToMainGraph()
            }
        )
    }
}

fun NavController.navigateToLoginScreen(navOptions: NavOptions? = null) =
    navigate(Screen.Login.route, navOptions)