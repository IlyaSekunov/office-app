package ru.ilyasekunov.officeapp.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation

fun NavGraphBuilder.mainGraph(navController: NavController) {
    navigation(
        route = MainGraphRoute,
        startDestination = BottomNavigationScreen.Home.route
    ) {

    }
}

fun NavController.navigateToMainGraph(navOptions: NavOptions? = null) =
    navigate(MainGraphRoute, navOptions)