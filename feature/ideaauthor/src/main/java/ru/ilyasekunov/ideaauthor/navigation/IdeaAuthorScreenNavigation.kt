package ru.ilyasekunov.ideaauthor.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import ru.ilyasekunov.ideaauthor.IdeaAuthorScreen
import ru.ilyasekunov.ideaauthor.IdeaAuthorViewModel
import ru.ilyasekunov.navigation.Screen

data object IdeaAuthorScreen : Screen(
    route = "idea-author/{authorId}",
    arguments = listOf(
        navArgument("authorId") { type = NavType.LongType }
    )
)

fun NavGraphBuilder.ideaAuthorScreen(
    navigateToIdeaDetailsScreen: (Long) -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    composable(
        route = IdeaAuthorScreen.route,
        arguments = IdeaAuthorScreen.arguments
    ) { backStackEntry ->
        val authorId = backStackEntry.arguments!!.getLong("authorId")
        val viewModel = setUpIdeaAuthorViewModel(authorId)
        val ideas = viewModel.authorIdeasUiState.data.collectAsLazyPagingItems()

        IdeaAuthorScreen(
            ideaAuthorUiState = viewModel.ideaAuthorUiState,
            ideas = ideas,
            onRetryLoadData = {
                viewModel.loadIdeaAuthor()
                ideas.retry()
            },
            onPullToRefresh = {
                launch {
                    launch { viewModel.refreshIdeaAuthor() }
                    ideas.refresh()
                }
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
    val destination = IdeaAuthorScreen.route.replace("{authorId}", authorId.toString())
    navigate(destination, navOptions)
}

@Composable
private fun setUpIdeaAuthorViewModel(authorId: Long): IdeaAuthorViewModel =
    hiltViewModel(
        creationCallback = { factory: IdeaAuthorViewModel.Factory ->
            factory.create(authorId)
        }
    )