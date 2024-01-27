package ru.ilyasekunov.officeapp.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import ru.ilyasekunov.officeapp.navigation.auth.navigateToAuthGraph

fun NavGraphBuilder.mainGraph(navController: NavController) {
    navigation(
        route = MainGraphRoute,
        startDestination = BottomNavigationScreen.Home.route
    ) {
        profileScreen(
            navigateToUserManageAccountScreen = { /*TODO*/ },
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
    }
}

fun NavController.navigateToMainGraph(navOptions: NavOptions? = null) =
    navigate(MainGraphRoute, navOptions)