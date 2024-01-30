package ru.ilyasekunov.officeapp.navigation.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import ru.ilyasekunov.officeapp.navigation.AuthGraphRoute
import ru.ilyasekunov.officeapp.navigation.Screen
import ru.ilyasekunov.officeapp.navigation.auth.login.loginScreen
import ru.ilyasekunov.officeapp.navigation.auth.login.navigateToLoginScreen
import ru.ilyasekunov.officeapp.navigation.auth.registration.navigateToRegistrationMainScreen
import ru.ilyasekunov.officeapp.navigation.auth.registration.navigateToRegistrationUserInfoScreen
import ru.ilyasekunov.officeapp.navigation.auth.registration.registrationMainScreen
import ru.ilyasekunov.officeapp.navigation.auth.registration.registrationUserInfoScreen
import ru.ilyasekunov.officeapp.navigation.navigateToMainGraph

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
            },
            navigateToMainGraph = {
                navController.navigateToMainGraph(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = AuthGraphRoute,
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
            viewModelStoreOwnerProvider = {
                navController.getBackStackEntry(Screen.RegistrationMain.route)
            },
            navigateBack = {
                navController.popBackStack(
                    route = Screen.RegistrationMain.route,
                    inclusive = false,
                    saveState = true
                )
            },
            navigateToMainGraph = {
                navController.navigateToMainGraph(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = AuthGraphRoute,
                            inclusive = true
                        )
                        .build()
                )
            }
        )
    }
}

fun NavController.navigateToAuthGraph(navOptions: NavOptions? = null) =
    navigate(AuthGraphRoute, navOptions)