package ru.ilyasekunov.officeapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import ru.ilyasekunov.officeapp.ui.ideaauthor.IdeaAuthorScreen
import ru.ilyasekunov.officeapp.ui.ideaauthor.IdeaAuthorViewModel

fun NavGraphBuilder.ideaAuthorScreen(
    navigateToIdeaDetailsScreen: (Long) -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    composable(
        route = Screen.IdeaAuthor.route,
        arguments = Screen.IdeaAuthor.arguments
    ) { backStackEntry ->
        val authorId = backStackEntry.arguments!!.getLong("authorId")
        val viewModel = setUpIdeaAuthorViewModel(authorId)
        val ideas = viewModel.authorIdeasUiState.ideas.collectAsLazyPagingItems()
        IdeaAuthorScreen(
            ideaAuthorUiState = viewModel.ideaAuthorUiState,
            ideas = ideas,
            onRetryLoadData = {
                viewModel.loadIdeaAuthorById(authorId)
                ideas.refresh()
            },
            onPullToRefresh = {
                viewModel.loadIdeaAuthorById(authorId)
                ideas.refresh()
            },
            onIdeaLikeClick = viewModel::updateLike,
            onIdeaDislikeClick = viewModel::updateDislike,
            navigateToIdeaDetailsScreen = navigateToIdeaDetailsScreen,
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToMyOfficeScreen = navigateToMyOfficeScreen,
            navigateToProfileScreen = navigateToProfileScreen,
            navigateBack = navigateBack
        )
    }
}

fun NavController.navigateToIdeaAuthorScreen(
    authorId: Long, navOptions: NavOptions? = null
) {
    val destination = Screen.IdeaAuthor.route.replace("{authorId}", authorId.toString())
    navigate(destination, navOptions)
}

@Composable
private fun setUpIdeaAuthorViewModel(authorId: Long): IdeaAuthorViewModel {
    val viewModel = hiltViewModel<IdeaAuthorViewModel>()
    LaunchedEffect(Unit) {
        viewModel.loadIdeaAuthorById(authorId)
        viewModel.loadIdeasByAuthorId(authorId)
    }
    return viewModel
}