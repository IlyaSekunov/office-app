package ru.ilyasekunov.officeapp.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.ui.animations.enterSlideUp
import ru.ilyasekunov.officeapp.ui.animations.exitSlideDown
import ru.ilyasekunov.officeapp.ui.suggestidea.SuggestIdeaScreen
import ru.ilyasekunov.officeapp.ui.suggestidea.SuggestIdeaViewModel

fun NavGraphBuilder.suggestIdeaScreen(
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    composable(
        route = Screen.SuggestIdea.route,
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
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToMyOfficeScreen = navigateToMyOfficeScreen,
            navigateToProfileScreen = navigateToProfileScreen,
            navigateBack = navigateBack
        )
    }
}

fun NavController.navigateToSuggestIdeaScreen(navOptions: NavOptions? = null) =
    navigate(Screen.SuggestIdea.route, navOptions)