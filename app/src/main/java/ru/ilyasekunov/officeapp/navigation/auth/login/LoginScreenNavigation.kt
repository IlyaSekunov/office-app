package ru.ilyasekunov.officeapp.navigation.auth.login

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.ui.auth.login.LoginScreen
import ru.ilyasekunov.officeapp.ui.auth.login.LoginViewModel

const val LoginRoute = "login"

fun NavGraphBuilder.loginScreen(
    navigateToRegistration: () -> Unit,
) {
    composable(LoginRoute) {
        val loginViewModel = hiltViewModel<LoginViewModel>()
        LoginScreen(
            loginUiState = loginViewModel.loginUiState,
            onEmailValueChange = loginViewModel::updateEmail,
            onPasswordValueChange = loginViewModel::updatePassword,
            onLoginButtonClick = loginViewModel::login,
            navigateToRegistration = navigateToRegistration
        )
    }
}

fun NavController.navigateToLoginScreen(navOptions: NavOptions? = null) =
    navigate(LoginRoute, navOptions)