package ru.ilyasekunov.officeapp.navigation.userprofile

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.navigation.BottomNavigationScreen
import ru.ilyasekunov.officeapp.ui.animations.enterSlideLeft
import ru.ilyasekunov.officeapp.ui.animations.exitSlideRight
import ru.ilyasekunov.officeapp.ui.userprofile.UserProfileScreen
import ru.ilyasekunov.officeapp.ui.userprofile.UserProfileViewModel

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
            onPullToRefresh = userProfileViewModel::refreshUserProfile,
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToMyOfficeScreen = navigateToMyOfficeScreen,
            navigateToAuthGraph = navigateToAuthGraph
        )
    }
}

fun NavController.navigateToProfileScreen(navOptions: NavOptions? = null) =
    navigate(BottomNavigationScreen.Profile.route, navOptions)