package ru.ilyasekunov.officeapp.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import ru.ilyasekunov.officeapp.navigation.auth.navigateToAuthGraph
import ru.ilyasekunov.officeapp.navigation.userprofile.navigateToProfileScreen
import ru.ilyasekunov.officeapp.navigation.userprofile.navigateToUserManageAccountScreen
import ru.ilyasekunov.officeapp.navigation.userprofile.profileScreen
import ru.ilyasekunov.officeapp.navigation.userprofile.userManageAccountScreen

fun NavGraphBuilder.mainGraph(navController: NavController) {
    navigation(
        route = MainGraphRoute,
        startDestination = BottomNavigationScreen.Home.route
    ) {
        homeScreen(
            navigateToIdeaDetailsScreen = { postId, initiallyScrollToComments ->
                navController.navigateToIdeaDetailsScreen(
                    postId = postId,
                    initiallyScrollToComments = initiallyScrollToComments,
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            },
            navigateToAuthorScreen = {
                navController.navigateToIdeaAuthorScreen(
                    authorId = it,
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            },
            navigateToEditIdeaScreen = { postId ->
                navController.navigateToEditIdeaScreen(
                    postId = postId,
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            },
            navigateToFavouriteScreen = {
                navController.navigateToFavouriteIdeasScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = BottomNavigationScreen.Favourite.route,
                            inclusive = true
                        )
                        .build()
                )
            },
            navigateToMyOfficeScreen = { /*TODO*/ },
            navigateToFiltersScreen = {
                navController.navigateToFiltersScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = Screen.FiltersScreen.route,
                            inclusive = true
                        )
                        .build()
                )
            },
            navigateToSuggestIdeaScreen = {
                navController.navigateToSuggestIdeaScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = Screen.SuggestIdea.route,
                            inclusive = true
                        )
                        .build()
                )
            },
            navigateToProfileScreen = {
                navController.navigateToProfileScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = BottomNavigationScreen.Profile.route,
                            inclusive = true
                        )
                        .build()
                )
            },
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
            }
        )
        favouriteIdeasScreen(
            navigateToFiltersScreen = {
                navController.navigateToFiltersScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = Screen.FiltersScreen.route,
                            inclusive = true
                        )
                        .build()
                )
            },
            navigateToIdeaDetailsScreen = { postId ->
                navController.navigateToIdeaDetailsScreen(
                    postId = postId,
                    initiallyScrollToComments = false,
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            },
            navigateToHomeScreen = {
                navController.navigateToHomeScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = BottomNavigationScreen.Home.route,
                            inclusive = false
                        )
                        .build()
                )
            },
            navigateToMyOfficeScreen = { /*TODO*/ },
            navigateToProfileScreen = {
                navController.navigateToProfileScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = BottomNavigationScreen.Profile.route,
                            inclusive = true
                        )
                        .build()
                )
            }
        )
        profileScreen(
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
            navigateToHomeScreen = {
                navController.navigateToHomeScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = BottomNavigationScreen.Home.route,
                            inclusive = false
                        )
                        .build()
                )
            },
            navigateToFavouriteScreen = {
                navController.navigateToFavouriteIdeasScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = BottomNavigationScreen.Home.route,
                            inclusive = false
                        )
                        .build()
                )
            }
        )
        userManageAccountScreen(
            navigateToHomeScreen = {
                navController.navigateToHomeScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = BottomNavigationScreen.Home.route,
                            inclusive = false
                        )
                        .build()
                )
            },
            navigateToFavouriteScreen = {
                navController.navigateToFavouriteIdeasScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            },
            navigateToMyOfficeScreen = { /*TODO*/ },
            navigateToProfileScreen = {
                navController.navigateToProfileScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = BottomNavigationScreen.Profile.route,
                            inclusive = true
                        )
                        .build()
                )
            },
            navigateBack = navController::popBackStack
        )
        filtersScreen(
            previousBackStackEntryProvider = {
                navController.previousBackStackEntry!!
            },
            navigateToHomeScreen = {
                navController.navigateToHomeScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = BottomNavigationScreen.Home.route,
                            inclusive = false
                        )
                        .build()
                )
            },
            navigateToFavouriteScreen = {
                navController.navigateToFavouriteIdeasScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            },
            navigateToMyOfficeScreen = {/* TODO */ },
            navigateToProfileScreen = {
                navController.navigateToProfileScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            },
            navigateBack = navController::popBackStack
        )
        suggestIdeaScreen(
            navigateToHomeScreen = {
                navController.navigateToHomeScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = BottomNavigationScreen.Home.route,
                            inclusive = false
                        )
                        .build()
                )
            },
            navigateToFavouriteScreen = {
                navController.navigateToFavouriteIdeasScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = BottomNavigationScreen.Favourite.route,
                            inclusive = true
                        )
                        .build()
                )
            },
            navigateToMyOfficeScreen = { /*TODO*/ },
            navigateToProfileScreen = {
                navController.navigateToProfileScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            },
            navigateBack = navController::popBackStack
        )
        editIdeaScreen(
            navigateToHomeScreen = {
                navController.navigateToHomeScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = BottomNavigationScreen.Home.route,
                            inclusive = false
                        )
                        .build()
                )
            },
            navigateToFavouriteScreen = {
                navController.navigateToFavouriteIdeasScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = BottomNavigationScreen.Favourite.route,
                            inclusive = true
                        )
                        .build()
                )
            },
            navigateToMyOfficeScreen = { /*TODO*/ },
            navigateToProfileScreen = {
                navController.navigateToProfileScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            },
            navigateBack = navController::popBackStack
        )
        ideaAuthorScreen(
            navigateToIdeaDetailsScreen = { postId ->
                navController.navigateToIdeaDetailsScreen(
                    postId = postId,
                    initiallyScrollToComments = false,
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = Screen.IdeaDetails.route,
                            inclusive = true
                        )
                        .build()
                )
            },
            navigateToHomeScreen = {
                navController.navigateToHomeScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = BottomNavigationScreen.Home.route,
                            inclusive = false
                        )
                        .build()
                )
            },
            navigateToFavouriteScreen = {
                navController.navigateToFavouriteIdeasScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = BottomNavigationScreen.Favourite.route,
                            inclusive = true
                        )
                        .build()
                )
            },
            navigateToMyOfficeScreen = { /*TODO*/ },
            navigateToProfileScreen = {
                navController.navigateToProfileScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            },
            navigateBack = navController::popBackStack
        )
        ideaDetailsScreen(
            navigateToIdeaAuthorScreen = {
                navController.navigateToIdeaAuthorScreen(
                    authorId = it,
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            },
            navigateBack = navController::popBackStack
        )
    }
}

fun NavController.navigateToMainGraph(navOptions: NavOptions? = null) =
    navigate(MainGraphRoute, navOptions)