package ru.ilyasekunov.officeapp.navigation.ideaauthor

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import ru.ilyasekunov.officeapp.navigation.Screen
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
    ) {backStackEntry ->
        val authorId = backStackEntry.arguments?.getLong("authorId")!!
        val ideaAuthorViewModel = hiltViewModel<IdeaAuthorViewModel>()
        LaunchedEffect(Unit) {
            ideaAuthorViewModel.loadIdeaAuthorById(authorId)
            ideaAuthorViewModel.loadIdeasByAuthorId(authorId)
        }

        val ideas = ideaAuthorViewModel.authorIdeasUiState.collectAsLazyPagingItems()
        IdeaAuthorScreen(
            ideaAuthorUiState = ideaAuthorViewModel.ideaAuthorUiState,
            ideas = ideas,
            onRetryLoadData = {
                ideaAuthorViewModel.loadIdeaAuthorById(authorId)
                ideas.refresh()
            },
            onPullToRefresh = {
                ideaAuthorViewModel.loadIdeaAuthorById(authorId)
                ideas.refresh()
            },
            onIdeaLikeClick = ideaAuthorViewModel::updateLike,
            onIdeaDislikeClick = ideaAuthorViewModel::updateDislike,
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