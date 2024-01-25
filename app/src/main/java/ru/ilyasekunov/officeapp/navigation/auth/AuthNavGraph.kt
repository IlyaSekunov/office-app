package ru.ilyasekunov.officeapp.navigation.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import ru.ilyasekunov.officeapp.navigation.Screen
import ru.ilyasekunov.officeapp.navigation.auth.login.loginScreen
import ru.ilyasekunov.officeapp.navigation.auth.login.navigateToLoginScreen
import ru.ilyasekunov.officeapp.navigation.auth.registration.navigateToRegistrationMainScreen
import ru.ilyasekunov.officeapp.navigation.auth.registration.navigateToRegistrationUserInfoScreen
import ru.ilyasekunov.officeapp.navigation.auth.registration.registrationMainScreen
import ru.ilyasekunov.officeapp.navigation.auth.registration.registrationUserInfoScreen

const val AuthGraphRoute = "auth"

fun NavGraphBuilder.authGraph(navController: NavController) {
    navigation(
        route = AuthGraphRoute,
        startDestination = Screen.Login.route
    ) {
        loginScreen(
            navigateToRegistration = {
                navController.navigateToRegistrationMainScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = Screen.RegistrationMain.route,
                            inclusive = true
                        )
                        .build()
                )
            }
        )
        registrationMainScreen(
            navigateToLogin = {
                navController.navigateToLoginScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = Screen.Login.route,
                            inclusive = true
                        )
                        .build()
                )
            },
            navigateToRegistrationUserInfo = {
                navController.navigateToRegistrationUserInfoScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setRestoreState(true)
                        .build()
                )
            }
        )
        registrationUserInfoScreen(
            navigateBack = {
                navController.popBackStack(
                    route = Screen.RegistrationMain.route,
                    inclusive = false,
                    saveState = true
                )
            }
        )
    }
}

fun NavController.navigateToAuthGraph(navOptions: NavOptions? = null) =
    navigate(AuthGraphRoute, navOptions)