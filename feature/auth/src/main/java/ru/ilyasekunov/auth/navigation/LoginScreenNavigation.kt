package ru.ilyasekunov.auth.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.auth.login.LoginScreen
import ru.ilyasekunov.auth.login.LoginViewModel
import ru.ilyasekunov.navigation.Screen

data object LoginScreen : Screen("login")

fun NavGraphBuilder.loginScreen(
    navigateToRegistration: () -> Unit,
    navigateToMainGraph: () -> Unit
) {
    composable(LoginScreen.route) {
        val loginViewModel = hiltViewModel<LoginViewModel>()
        LoginScreen(
            loginUiState = loginViewModel.loginUiState,
            onEmailValueChange = loginViewModel::updateEmail,
            onPasswordValueChange = loginViewModel::updatePassword,
            onLoginButtonClick = loginViewModel::login,
            onCredentialsInvalidShown = loginViewModel::invalidCredentialsShown,
            onNetworkErrorShown = loginViewModel::networkErrorShown,
            navigateToRegistrationMainScreen = navigateToRegistration,
            navigateToHomeScreen = navigateToMainGraph
        )
    }
}

fun NavController.navigateToLoginScreen(navOptions: NavOptions? = null) =
    navigate(LoginScreen.route, navOptions)