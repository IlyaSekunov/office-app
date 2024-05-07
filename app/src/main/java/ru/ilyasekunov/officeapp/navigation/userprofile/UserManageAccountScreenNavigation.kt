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
        val viewModel = hiltViewModel<UserManageAccountViewModel>()
        UserManageAccountScreen(
            userManageAccountUiState = viewModel.userManageAccountUiState,
            onAttachImage = viewModel::updatePhoto,
            onNameValueChange = viewModel::updateName,
            onSurnameValueChange = viewModel::updateSurname,
            onJobValueChange = viewModel::updateJob,
            onOfficeChange = viewModel::updateOffice,
            onRetrySaveClick = viewModel::save,
            onRetryLoadProfileClick = {
                viewModel.loadUserProfile()
                viewModel.loadAvailableOffices()
            },
            onChangesSavingErrorShown = viewModel::changesSavingErrorShown,
            onSaveButtonClick = viewModel::save,
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