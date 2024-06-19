package ru.ilyasekunov.suggestidea.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.navigation.Screen
import ru.ilyasekunov.navigation.enterSlideUp
import ru.ilyasekunov.navigation.exitSlideDown
import ru.ilyasekunov.suggestidea.SuggestIdeaScreen
import ru.ilyasekunov.suggestidea.SuggestIdeaViewModel

data object SuggestIdeaScreen : Screen("suggest-idea")

fun NavGraphBuilder.suggestIdeaScreen(
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    composable(
        route = SuggestIdeaScreen.route,
        enterTransition = { enterSlideUp() },
        exitTransition = { exitSlideDown() }
    ) {
        val viewModel = hiltViewModel<SuggestIdeaViewModel>()
        SuggestIdeaScreen(
            suggestIdeaUiState = viewModel.suggestIdeaUiState,
            onTitleValueChange = viewModel::updateTitle,
            onIdeaBodyValueChange = viewModel::updateContent,
            onRemoveImageClick = viewModel::removeImage,
            onPublishClick = viewModel::publishPost,
            onAttachImagesButtonClick = viewModel::attachImages,
            onRetryClick = viewModel::publishPost,
            onNetworkErrorShown = viewModel::networkErrorShown,
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToMyOfficeScreen = navigateToMyOfficeScreen,
            navigateToProfileScreen = navigateToProfileScreen,
            navigateBack = navigateBack
        )
    }
}

fun NavController.navigateToSuggestIdeaScreen(navOptions: NavOptions? = null) =
    navigate(SuggestIdeaScreen.route, navOptions)