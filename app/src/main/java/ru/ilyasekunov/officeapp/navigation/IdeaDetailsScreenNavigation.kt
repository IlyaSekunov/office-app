package ru.ilyasekunov.officeapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.ui.animations.enterSlideLeft
import ru.ilyasekunov.officeapp.ui.animations.exitSlideRight
import ru.ilyasekunov.officeapp.ui.ideadetails.IdeaDetailsScreen
import ru.ilyasekunov.officeapp.ui.ideadetails.IdeaDetailsViewModel

fun NavGraphBuilder.ideaDetailsScreen(
    navigateToIdeaAuthorScreen: (authorId: Long) -> Unit,
    navigateBack: () -> Unit
) {
    composable(
        route = Screen.IdeaDetails.route,
        arguments = Screen.IdeaDetails.arguments,
        enterTransition = { enterSlideLeft() },
        exitTransition = { exitSlideRight() }
    ) { backStackEntry ->
        val navArguments = backStackEntry.arguments!!
        val postId = navArguments.getLong("postId")
        val initiallyScrollToComments = navArguments.getBoolean("initiallyScrollToComments")
        val viewModel = setUpIdeaDetailsViewModel(postId)
        val comments = viewModel.commentsUiState.comments.collectAsLazyPagingItems()
        IdeaDetailsScreen(
            currentUserUiState = viewModel.currentUserUiState,
            ideaPostUiState = viewModel.ideaPostUiState,
            sendingMessageUiState = viewModel.sendingMessageUiState,
            currentCommentsSortingFilter = viewModel.commentsUiState.currentSortingFilter,
            currentEditableComment = viewModel.currentEditableComment,
            comments = comments,
            commentDeletingUiState = viewModel.commentDeletingUiState,
            initiallyScrollToComments = initiallyScrollToComments,
            onCommentsFilterSelect = viewModel::updateCommentsSortingFilter,
            onRetryInfoLoad = {
                viewModel.loadCurrentUser()
                viewModel.loadPostById(postId)
            },
            onRetryCommentsLoad = { viewModel.loadCommentsByPostId(postId) },
            onPullToRefresh = {
                launch {
                    launch {
                        viewModel.refreshCurrentUser()
                    }
                    launch {
                        viewModel.refreshPostById(postId)
                    }
                    launch {
                        comments.refresh()
                    }
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
            onSendCommentClick = viewModel::sendComment,
            onDeleteCommentClick = viewModel::deleteComment,
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
    val destination = Screen.IdeaDetails.route
        .replace("{postId}", "$postId")
        .replace("{initiallyScrollToComments}", "$initiallyScrollToComments")
    navigate(destination, navOptions)
}

@Composable
private fun setUpIdeaDetailsViewModel(postId: Long): IdeaDetailsViewModel {
    val viewModel = hiltViewModel<IdeaDetailsViewModel>()
    LaunchedEffect(Unit) {
        viewModel.loadPostById(postId)
        viewModel.loadCommentsByPostId(postId)
    }
    return viewModel
}