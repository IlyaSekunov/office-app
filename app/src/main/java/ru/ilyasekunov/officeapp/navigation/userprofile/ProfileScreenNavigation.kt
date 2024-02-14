package ru.ilyasekunov.officeapp.navigation.userprofile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.navigation.BottomNavigationScreen
import ru.ilyasekunov.officeapp.ui.animations.enterSlideLeft
import ru.ilyasekunov.officeapp.ui.animations.exitSlideRight
import ru.ilyasekunov.officeapp.ui.userprofile.UserInfoUiState
import ru.ilyasekunov.officeapp.ui.userprofile.UserProfileScreen
import ru.ilyasekunov.officeapp.ui.userprofile.UserViewModel
import ru.ilyasekunov.officeapp.ui.userprofile.toUserInfoUiState

fun NavGraphBuilder.profileScreen(
    viewModelStoreOwnerProvider: () -> ViewModelStoreOwner,
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
    ) { backStackEntry ->
        val viewModelStoreOwner = remember(backStackEntry) { viewModelStoreOwnerProvider() }
        val userInfoViewModel = hiltViewModel<UserViewModel>(viewModelStoreOwner)
        val user = userInfoViewModel.user!!
        val userInfoUiState by rememberSaveable(stateSaver = UserInfoUiState.Saver) {
            mutableStateOf(user.toUserInfoUiState())
        }
        UserProfileScreen(
            userInfoUiState = userInfoUiState,
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