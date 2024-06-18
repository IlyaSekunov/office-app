package ru.ilyasekunov.officeapp.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import ru.ilyasekunov.auth.navigation.navigateToAuthGraph
import ru.ilyasekunov.editidea.navigation.editIdeaScreen
import ru.ilyasekunov.editidea.navigation.navigateToEditIdeaScreen
import ru.ilyasekunov.favouriteideas.FavouriteIdeasViewModel
import ru.ilyasekunov.favouriteideas.navigation.favouriteIdeasScreen
import ru.ilyasekunov.favouriteideas.navigation.navigateToFavouriteIdeasScreen
import ru.ilyasekunov.filters.navigation.FiltersScreen
import ru.ilyasekunov.filters.navigation.filtersScreen
import ru.ilyasekunov.filters.navigation.navigateToFiltersScreen
import ru.ilyasekunov.home.HomeViewModel
import ru.ilyasekunov.home.navigation.homeScreen
import ru.ilyasekunov.home.navigation.navigateToHomeScreen
import ru.ilyasekunov.ideaauthor.navigation.ideaAuthorScreen
import ru.ilyasekunov.ideaauthor.navigation.navigateToIdeaAuthorScreen
import ru.ilyasekunov.ideadetails.navigation.ideaDetailsScreen
import ru.ilyasekunov.ideadetails.navigation.navigateToIdeaDetailsScreen
import ru.ilyasekunov.manage.navigation.navigateToUserManageAccountScreen
import ru.ilyasekunov.manage.navigation.userManageAccountScreen
import ru.ilyasekunov.myideas.navigation.myIdeasScreen
import ru.ilyasekunov.myideas.navigation.navigateToMyIdeasScreen
import ru.ilyasekunov.navigation.BottomNavigationScreen
import ru.ilyasekunov.office.navigation.myOfficeScreen
import ru.ilyasekunov.office.navigation.navigateToMyOfficeScreen
import ru.ilyasekunov.profile.navigation.navigateToProfileScreen
import ru.ilyasekunov.profile.navigation.profileScreen
import ru.ilyasekunov.suggestidea.navigation.SuggestIdeaScreen
import ru.ilyasekunov.suggestidea.navigation.navigateToSuggestIdeaScreen
import ru.ilyasekunov.suggestidea.navigation.suggestIdeaScreen

const val MainGraphRoute = "app-graph"

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
            navigateToMyOfficeScreen = {
                navController.navigateToMyOfficeScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = BottomNavigationScreen.MyOffice.route,
                            inclusive = true
                        )
                        .build()
                )
            },
            navigateToFiltersScreen = {
                navController.navigateToFiltersScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = FiltersScreen.route,
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
                            route = SuggestIdeaScreen.route,
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
                            route = FiltersScreen.route,
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
            navigateToMyOfficeScreen = {
                navController.navigateToMyOfficeScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
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
            navigateToMyOfficeScreen = {
                navController.navigateToMyOfficeScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            },
            navigateToMyIdeasScreen = {
                navController.navigateToMyIdeasScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
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
            navigateToMyOfficeScreen = {
                navController.navigateToMyOfficeScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
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
            navigateBack = navController::popBackStack
        )
        filtersScreen(
            filtersUiStateHolderProvider = {
                val previousBackStackEntry = navController.previousBackStackEntry
                when (previousBackStackEntry?.destination?.route) {
                    BottomNavigationScreen.Home.route -> {
                        hiltViewModel<HomeViewModel>(previousBackStackEntry).filtersUiStateHolder
                    }

                    BottomNavigationScreen.Favourite.route -> {
                        hiltViewModel<FavouriteIdeasViewModel>(previousBackStackEntry).filtersUiStateHolder
                    }

                    else -> null
                }
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
            navigateToMyOfficeScreen = {
                navController.navigateToMyOfficeScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            },
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
            navigateToMyOfficeScreen = {
                navController.navigateToMyOfficeScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            },
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
            navigateToMyOfficeScreen = {
                navController.navigateToMyOfficeScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            },
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
            navigateToMyOfficeScreen = {
                navController.navigateToMyOfficeScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            },
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
        myOfficeScreen(
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
        myIdeasScreen(
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
            navigateToMyOfficeScreen = {
                navController.navigateToMyOfficeScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            },
            navigateToProfileScreen = {
                navController.navigateToProfileScreen(
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = BottomNavigationScreen.Profile.route,
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
            navigateToSuggestIdeaScreen = {
                navController.navigateToSuggestIdeaScreen(
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
            navigateBack = navController::popBackStack
        )
    }
}

fun NavController.navigateToMainGraph(navOptions: NavOptions? = null) =
    navigate(MainGraphRoute, navOptions)