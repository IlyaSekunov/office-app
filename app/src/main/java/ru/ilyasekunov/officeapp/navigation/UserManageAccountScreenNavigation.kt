package ru.ilyasekunov.officeapp.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.ui.userprofile.UserManageAccountScreen
import ru.ilyasekunov.officeapp.ui.userprofile.UserViewModel

fun NavGraphBuilder.userManageAccountScreen(
    viewModelStoreOwnerProvider: () -> ViewModelStoreOwner,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    composable(route = Screen.UserManageAccount.route) { backStackEntry ->
        val viewModelStoreOwner = remember(backStackEntry) { viewModelStoreOwnerProvider() }
        val userInfoViewModel = hiltViewModel<UserViewModel>(viewModelStoreOwner)
        val galleryLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
                userInfoViewModel.updatePhotoUri(it.toString())
            }
        UserManageAccountScreen(
            userInfoUiState = userInfoViewModel.userInfoUiState,
            officeList = userInfoViewModel.officeList,
            onPhotoPickerClick = { galleryLauncher.launch("image/*") },
            onNameValueChange = userInfoViewModel::updateName,
            onSurnameValueChange = userInfoViewModel::updateSurname,
            onJobValueChange = userInfoViewModel::updateJob,
            onOfficeChange = userInfoViewModel::updateOffice,
            onSaveButtonClick = {
                userInfoViewModel.save()
                navigateToProfileScreen()
            },
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToMyOfficeScreen = navigateToMyOfficeScreen,
            navigateToProfileScreen = navigateToProfileScreen,
            navigateBack = navigateBack
        )
    }
}

fun NavController.navigateToUserManageAccountScreen(navOptions: NavOptions? = null) =
    navigate(Screen.UserManageAccount.route, navOptions)