package ru.ilyasekunov.ideadetails.navigation

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
import ru.ilyasekunov.ideadetails.IdeaDetailsScreen
import ru.ilyasekunov.ideadetails.IdeaDetailsViewModel
import ru.ilyasekunov.navigation.Screen
import ru.ilyasekunov.navigation.enterSlideLeft
import ru.ilyasekunov.navigation.exitSlideRight

data object IdeaDetailsScreen : Screen(
    route = "idea-details/{postId}?initiallyScrollToComments={initiallyScrollToComments}",
    arguments = listOf(
        navArgument("postId") { type = NavType.LongType },
        navArgument("initiallyScrollToComments") {
            type = NavType.BoolType
            defaultValue = false
        }
    )
)

fun NavGraphBuilder.ideaDetailsScreen(
    navigateToIdeaAuthorScreen: (authorId: Long) -> Unit,
    navigateBack: () -> Unit
) {
    composable(
        route = IdeaDetailsScreen.route,
        arguments = IdeaDetailsScreen.arguments,
        enterTransition = { enterSlideLeft() },
        exitTransition = { exitSlideRight() }
    ) { backStackEntry ->
        val navArguments = backStackEntry.arguments!!
        val postId = navArguments.getLong("postId")
        val initiallyScrollToComments = navArguments.getBoolean("initiallyScrollToComments")
        val viewModel = setUpIdeaDetailsViewModel(postId)
        val comments = viewModel.commentsUiState.comments.data.collectAsLazyPagingItems()

        IdeaDetailsScreen(
            currentUserUiState = viewModel.currentUserUiState,
            ideaPostUiState = viewModel.ideaPostUiState,
            sendMessageUiState = viewModel.sendMessageUiState,
            currentCommentsSortingFilter = viewModel.commentsUiState.currentSortingFilter,
            currentEditableComment = viewModel.currentEditableComment,
            comments = comments,
            deleteCommentUiState = viewModel.deleteCommentUiState,
            initiallyScrollToComments = initiallyScrollToComments,
            onCommentsFilterSelect = viewModel::updateCommentsSortingFilter,
            onRetryInfoLoad = {
                viewModel.loadData()
                comments.retry()
            },
            onPullToRefresh = {
                launch {
                    launch { viewModel.refreshCurrentUser() }
                    launch { viewModel.refreshPost() }
                    comments.refresh()
                }
            },
            onCommentLikeClick = viewModel::updateCommentLike,
            onCommentDislikeClick = viewModel::updateCommentDislike,
            onEditCommentClick = viewModel::startEditComment,
            onEditCommentDismiss = viewModel::stopEditComment,
            onLikeClick = viewModel::updatePostLike,
            onDislikeClick = viewModel::updatePostDislike,
            onMessageValueChange = viewModel::updateMessage,
            onAttachImage = viewModel::attachImage,
            onRemoveImageClick = viewModel::removeImage,
            onPublishCommentClick = viewModel::sendComment,
            onPublishCommentResultShown = viewModel::publishCommentResultShown,
            onDeleteCommentClick = viewModel::deleteComment,
            onDeleteCommentResultShown = viewModel::deleteCommentResultShown,
            navigateToIdeaAuthorScreen = navigateToIdeaAuthorScreen,
            navigateBack = navigateBack
        )
    }
}

fun NavController.navigateToIdeaDetailsScreen(
    postId: Long,
    initiallyScrollToComments: Boolean = false,
    navOptions: NavOptions? = null
) {
    val destination = IdeaDetailsScreen.route
        .replace("{postId}", "$postId")
        .replace("{initiallyScrollToComments}", "$initiallyScrollToComments")
    navigate(destination, navOptions)
}

@Composable
private fun setUpIdeaDetailsViewModel(postId: Long): IdeaDetailsViewModel =
    hiltViewModel(
        creationCallback = { factory: IdeaDetailsViewModel.Factory ->
            factory.create(postId)
        }
    )