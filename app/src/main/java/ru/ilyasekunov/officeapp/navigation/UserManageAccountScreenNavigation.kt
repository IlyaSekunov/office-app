package ru.ilyasekunov.officeapp.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.MainActivity
import ru.ilyasekunov.officeapp.ui.userprofile.UserInfoViewModel
import ru.ilyasekunov.officeapp.ui.userprofile.UserManageAccountScreen

fun NavGraphBuilder.userManageAccountScreen(
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    composable(route = Screen.UserManageAccount.route) {
        val userInfoViewModel = viewModel<UserInfoViewModel>(LocalContext.current as MainActivity)
        val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let {
                userInfoViewModel.updatePhotoUri(it.toString())
            }
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