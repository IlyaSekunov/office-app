package ru.ilyasekunov.officeapp.navigation.auth.registration

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import ru.ilyasekunov.officeapp.navigation.auth.login.LoginRoute
import ru.ilyasekunov.officeapp.navigation.auth.login.navigateToLoginScreen

const val RegistrationGraphRoute = "registration"

fun NavGraphBuilder.registrationGraph(navController: NavController) {
    navigation(
        route = RegistrationGraphRoute,
        startDestination = RegistrationMainRoute
    ) {
        registrationMainScreen(
            navigateToLogin = {
                navController.navigateToLoginScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = LoginRoute,
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
                    route = RegistrationMainRoute,
                    inclusive = false,
                    saveState = true
                )
            }
        )
    }
}

fun NavController.navigateToRegistrationGraph(navOptions: NavOptions? = null) =
    navigate(RegistrationGraphRoute, navOptions)