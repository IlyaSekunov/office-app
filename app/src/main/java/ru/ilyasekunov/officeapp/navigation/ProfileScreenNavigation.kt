package ru.ilyasekunov.officeapp.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.ui.userprofile.UserInfoViewModel
import ru.ilyasekunov.officeapp.ui.userprofile.UserProfileScreen

fun NavGraphBuilder.profileScreen(
    navigateToUserManageAccountScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToMyIdeasScreen: () -> Unit,
    navigateToAuthGraph: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit
) {
    composable(route = BottomNavigationScreen.Profile.route) {
        val userInfoViewModel = hiltViewModel<UserInfoViewModel>()
        UserProfileScreen(
            userInfoUiState = userInfoViewModel.userInfoUiState,
            onManageAccountClick = navigateToUserManageAccountScreen,
            onMyOfficeClick = navigateToMyOfficeScreen,
            onMyIdeasClick = navigateToMyIdeasScreen,
            onLogoutClick = {
                userInfoViewModel.logout()
                navigateToAuthGraph()
            },
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToMyOfficeScreen = navigateToMyOfficeScreen
        )
    }
}

fun NavController.navigateToProfileScreen(navOptions: NavOptions? = null) =
    navigate(BottomNavigationScreen.Profile.route, navOptions)