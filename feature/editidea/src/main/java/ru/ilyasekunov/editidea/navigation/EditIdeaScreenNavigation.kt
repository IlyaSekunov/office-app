package ru.ilyasekunov.editidea.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.ilyasekunov.editidea.EditIdeaScreen
import ru.ilyasekunov.editidea.EditIdeaViewModel
import ru.ilyasekunov.navigation.Screen
import ru.ilyasekunov.navigation.enterSlideUp
import ru.ilyasekunov.navigation.exitSlideDown

data object EditIdeaScreen : Screen(
    route = "edit-idea/{postId}",
    arguments = listOf(
        navArgument("postId") { type = NavType.LongType }
    )
)

fun NavGraphBuilder.editIdeaScreen(
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    composable(
        route = EditIdeaScreen.route,
        arguments = EditIdeaScreen.arguments,
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
    val destination = EditIdeaScreen.route.replace("{postId}", postId.toString())
    navigate(destination, navOptions)
}

@Composable
private fun setUpEditIdeaViewModel(postId: Long): EditIdeaViewModel =
    hiltViewModel(
        creationCallback = { factory: EditIdeaViewModel.Factory ->
            factory.create(postId)
        }
    )