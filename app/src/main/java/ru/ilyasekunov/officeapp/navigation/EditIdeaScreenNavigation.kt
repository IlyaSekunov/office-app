package ru.ilyasekunov.officeapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.ui.animations.enterSlideUp
import ru.ilyasekunov.officeapp.ui.animations.exitSlideDown
import ru.ilyasekunov.officeapp.ui.editidea.EditIdeaScreen
import ru.ilyasekunov.officeapp.ui.editidea.EditIdeaViewModel

fun NavGraphBuilder.editIdeaScreen(
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    composable(
        route = Screen.EditIdea.route,
        arguments = Screen.EditIdea.arguments,
        enterTransition = { enterSlideUp() },
        exitTransition = { exitSlideDown() }
    ) { backStackEntry ->
        val postId = backStackEntry.arguments!!.getLong("postId")
        val viewModel = setUpEditIdeaViewModel(postId)
        EditIdeaScreen(
            editIdeaUiState = viewModel.editIdeaUiState,
            onTitleValueChange = viewModel::updateTitle,
            onIdeaBodyValueChange = viewModel::updateContent,
            onRemoveImageClick = viewModel::removeImage,
            onPublishClick = viewModel::editPost,
            onAttachImagesButtonClick = viewModel::attachImages,
            onRetryClick = viewModel::editPost,
            onRetryLoadPost = viewModel::loadPost,
            onErrorWhilePublishingShown = viewModel::errorWhilePublishingShown,
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToMyOfficeScreen = navigateToMyOfficeScreen,
            navigateToProfileScreen = navigateToProfileScreen,
            navigateBack = navigateBack
        )
    }
}

fun NavController.navigateToEditIdeaScreen(
    postId: Long, navOptions: NavOptions? = null
) {
    val destination = Screen.EditIdea.route.replace("{postId}", postId.toString())
    navigate(destination, navOptions)
}

@Composable
fun setUpEditIdeaViewModel(postId: Long): EditIdeaViewModel =
    hiltViewModel(
        creationCallback = { factory: EditIdeaViewModel.Factory ->
            factory.create(postId)
        }
    )