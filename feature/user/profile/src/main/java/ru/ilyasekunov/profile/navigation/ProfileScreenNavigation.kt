package ru.ilyasekunov.profile.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import ru.ilyasekunov.navigation.BottomNavigationScreen
import ru.ilyasekunov.navigation.enterSlideLeft
import ru.ilyasekunov.navigation.exitSlideRight
import ru.ilyasekunov.profile.UserProfileScreen
import ru.ilyasekunov.profile.UserProfileViewModel

fun NavGraphBuilder.profileScreen(
    navigateToUserManageAccountScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToMyIdeasScreen: () -> Unit,
    navigateToAuthGraph: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit
) {
    composable(
        route = BottomNavigationScreen.Profile.route,
        enterTransition = { enterSlideLeft() },
        exitTransition = { exitSlideRight() }
    ) {
        val userProfileViewModel = hiltViewModel<UserProfileViewModel>()
        UserProfileScreen(
            userProfileUiState = userProfileViewModel.userProfileUiState,
            onManageAccountClick = navigateToUserManageAccountScreen,
            onMyOfficeClick = navigateToMyOfficeScreen,
            onMyIdeasClick = navigateToMyIdeasScreen,
            onLogoutClick = userProfileViewModel::logout,
            onRetryUserLoadClick = userProfileViewModel::loadUserProfile,
            onPullToRefresh = {
                launch {
                    userProfileViewModel.refreshUserProfile()
                }
            },
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToMyOfficeScreen = navigateToMyOfficeScreen,
            navigateToAuthGraph = navigateToAuthGraph
        )
    }
}

fun NavController.navigateToProfileScreen(navOptions: NavOptions? = null) =
    navigate(BottomNavigationScreen.Profile.route, navOptions)