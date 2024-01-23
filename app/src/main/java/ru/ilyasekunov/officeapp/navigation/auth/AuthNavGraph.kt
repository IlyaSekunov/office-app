package ru.ilyasekunov.officeapp.navigation.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import ru.ilyasekunov.officeapp.navigation.auth.login.LoginRoute
import ru.ilyasekunov.officeapp.navigation.auth.login.loginScreen
import ru.ilyasekunov.officeapp.navigation.auth.registration.RegistrationGraphRoute
import ru.ilyasekunov.officeapp.navigation.auth.registration.navigateToRegistrationGraph
import ru.ilyasekunov.officeapp.navigation.auth.registration.registrationGraph

const val AuthGraphRoute = "auth"

fun NavGraphBuilder.authGraph(navController: NavController) {
    navigation(
        route = AuthGraphRoute,
        startDestination = LoginRoute
    ) {
        loginScreen(
            navigateToRegistration = {
                navController.navigateToRegistrationGraph(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = RegistrationGraphRoute,
                            inclusive = true
                        )
                        .build()
                )
            }
        )
        registrationGraph(navController)
    }
}

fun NavController.navigateToAuthGraph(navOptions: NavOptions? = null) =
    navigate(AuthGraphRoute, navOptions)