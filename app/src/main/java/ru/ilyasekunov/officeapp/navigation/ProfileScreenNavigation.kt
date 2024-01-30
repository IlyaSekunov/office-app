package ru.ilyasekunov.officeapp.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.ui.userprofile.UserProfileScreen
import ru.ilyasekunov.officeapp.ui.userprofile.UserViewModel

fun NavGraphBuilder.profileScreen(
    viewModelStoreOwnerProvider: () -> ViewModelStoreOwner,
    navigateToUserManageAccountScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToMyIdeasScreen: () -> Unit,
    navigateToAuthGraph: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit
) {
    composable(route = BottomNavigationScreen.Profile.route) { backStackEntry ->
        val viewModelStoreOwner = remember(backStackEntry) { viewModelStoreOwnerProvider() }
        val userInfoViewModel = hiltViewModel<UserViewModel>(viewModelStoreOwner)
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