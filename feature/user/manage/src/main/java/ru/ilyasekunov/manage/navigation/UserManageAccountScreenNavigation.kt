package ru.ilyasekunov.manage.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.manage.UserManageAccountScreen
import ru.ilyasekunov.manage.UserManageAccountViewModel
import ru.ilyasekunov.navigation.Screen
import ru.ilyasekunov.navigation.enterSlideLeft
import ru.ilyasekunov.navigation.exitSlideRight

data object UserManageAccountScreen : Screen("user-manage-account")

fun NavGraphBuilder.userManageAccountScreen(
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    composable(
        route = UserManageAccountScreen.route,
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
    navigate(UserManageAccountScreen.route, navOptions)