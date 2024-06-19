package ru.ilyasekunov.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation

const val AuthGraphRoute = "auth-graph"

fun NavGraphBuilder.authGraph(
    navController: NavController,
    navigateToMainGraph: () -> Unit
) {
    navigation(
        route = AuthGraphRoute,
        startDestination = LoginScreen.route
    ) {
        loginScreen(
            navigateToRegistration = {
                navController.navigateToRegistrationMainScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = RegistrationMainScreen.route,
                            inclusive = true
                        )
                        .build()
                )
            },
            navigateToMainGraph = navigateToMainGraph
        )
        registrationMainScreen(
            navigateToLoginScreen = {
                navController.navigateToLoginScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = LoginScreen.route,
                            inclusive = true
                        )
                        .build()
                )
            },
            navigateToRegistrationUserInfoScreen = {
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
                navController.getBackStackEntry(RegistrationMainScreen.route)
            },
            navigateBack = {
                navController.popBackStack(
                    route = RegistrationMainScreen.route,
                    inclusive = false,
                    saveState = true
                )
            },
            navigateToMainGraph = navigateToMainGraph
        )
    }
}

fun NavController.navigateToAuthGraph(navOptions: NavOptions? = null) =
    navigate(AuthGraphRoute, navOptions)