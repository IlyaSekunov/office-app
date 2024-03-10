package ru.ilyasekunov.officeapp.navigation.userprofile

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.navigation.Screen
import ru.ilyasekunov.officeapp.ui.animations.enterSlideLeft
import ru.ilyasekunov.officeapp.ui.animations.exitSlideRight
import ru.ilyasekunov.officeapp.ui.userprofile.UserManageAccountScreen
import ru.ilyasekunov.officeapp.ui.userprofile.UserManageAccountViewModel
import ru.ilyasekunov.officeapp.ui.imagepickers.rememberSingleImagePicker

fun NavGraphBuilder.userManageAccountScreen(
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    composable(
        route = Screen.UserManageAccount.route,
        enterTransition = { enterSlideLeft() },
        exitTransition = { exitSlideRight() }
    ) {
        val userManageAccountViewModel = hiltViewModel<UserManageAccountViewModel>()
        val singleImagePicker = rememberSingleImagePicker(
            onUriPicked = userManageAccountViewModel::updatePhoto
        )
        UserManageAccountScreen(
            userManageAccountUiState = userManageAccountViewModel.userManageAccountUiState,
            onPhotoPickerClick = singleImagePicker::launch,
            onNameValueChange = userManageAccountViewModel::updateName,
            onSurnameValueChange = userManageAccountViewModel::updateSurname,
            onJobValueChange = userManageAccountViewModel::updateJob,
            onOfficeChange = userManageAccountViewModel::updateOffice,
            onRetrySaveClick = userManageAccountViewModel::save,
            onRetryLoadProfileClick = {
                userManageAccountViewModel.loadUserProfile()
                userManageAccountViewModel.loadAvailableOffices()
            },
            onSaveButtonClick = userManageAccountViewModel::save,
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