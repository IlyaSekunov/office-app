package ru.ilyasekunov.officeapp.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import ru.ilyasekunov.officeapp.navigation.auth.navigateToAuthGraph

fun NavGraphBuilder.mainGraph(navController: NavController) {
    navigation(
        route = MainGraphRoute,
        startDestination = BottomNavigationScreen.Profile.route // BottomNavigationScreen.Home.route
    ) {
        profileScreen(
            viewModelStoreOwnerProvider = {
                navController.getBackStackEntry(MainGraphRoute)
            },
            navigateToUserManageAccountScreen = {
                navController.navigateToUserManageAccountScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            },
            navigateToMyOfficeScreen = { /*TODO*/ },
            navigateToMyIdeasScreen = { /*TODO*/ },
            navigateToAuthGraph = {
                navController.navigateToAuthGraph(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = MainGraphRoute,
                            inclusive = true
                        )
                        .build()
                )
            },
            navigateToHomeScreen = { /*TODO*/ },
            navigateToFavouriteScreen = { /*TODO*/ }
        )
        userManageAccountScreen(
            viewModelStoreOwnerProvider = {
                navController.getBackStackEntry(MainGraphRoute)
            },
            navigateToHomeScreen = { /*TODO*/ },
            navigateToFavouriteScreen = { /*TODO*/ },
            navigateToMyOfficeScreen = { /*TODO*/ },
            navigateToProfileScreen = {
                navController.navigateToProfileScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = Screen.UserManageAccount.route,
                            inclusive = true
                        )
                        .build()
                )
            },
            navigateBack = navController::popBackStack
        )
    }
}

fun NavController.navigateToMainGraph(navOptions: NavOptions? = null) =
    navigate(MainGraphRoute, navOptions)