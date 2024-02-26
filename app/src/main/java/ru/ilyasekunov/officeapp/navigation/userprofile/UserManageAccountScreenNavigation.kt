package ru.ilyasekunov.officeapp.navigation.userprofile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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

        // Initialize image picker
        val singleImagePicker =
            rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
                userManageAccountViewModel.updatePhoto(it)
            }

        UserManageAccountScreen(
            userManageAccountUiState = userManageAccountViewModel.userManageAccountUiState,
            onPhotoPickerClick = {
                singleImagePicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onNameValueChange = userManageAccountViewModel::updateName,
            onSurnameValueChange = userManageAccountViewModel::updateSurname,
            onJobValueChange = userManageAccountViewModel::updateJob,
            onOfficeChange = userManageAccountViewModel::updateOffice,
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