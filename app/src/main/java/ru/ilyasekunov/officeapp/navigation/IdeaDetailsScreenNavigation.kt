package ru.ilyasekunov.officeapp.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
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
        enterTransition = { enterSlideLeft() },
        exitTransition = { exitSlideRight() }
    ) { backStackEntry ->
        val navArguments = remember(backStackEntry) { backStackEntry.arguments!! }
        val postId = rememberPostId(navArguments)
        val initiallyScrollToComments = rememberInitiallyScrollToComments(navArguments)
        val viewModel = setUpIdeaDetailsViewModel(postId)
        val comments = viewModel.commentsUiState.comments.collectAsLazyPagingItems()
        IdeaDetailsScreen(
            ideaPostUiState = viewModel.ideaPostUiState,
            sendingMessageUiState = viewModel.sendingMessageUiState,
            currentCommentsSortingFilter = viewModel.commentsUiState.currentSortingFilter,
            onCommentsFilterSelect = viewModel::updateCommentsSortingFilter,
            onRetryPostLoad = { viewModel.loadPostById(postId) },
            onRetryCommentsLoad = { viewModel.loadCommentsByPostId(postId) },
            onPullToRefresh = {
                viewModel.loadPostByIdSuspending(postId)
                comments.refresh()
            },
            comments = comments,
            onCommentLikeClick = viewModel::updateCommentLike,
            onCommentDislikeClick = viewModel::updateCommentDislike,
            onLikeClick = viewModel::updatePostLike,
            onDislikeClick = viewModel::updatePostDislike,
            onMessageValueChange = viewModel::updateMessage,
            onAttachImage = viewModel::attachImage,
            onRemoveImageClick = viewModel::removeImage,
            onSendCommentClick = viewModel::sendComment,
            initiallyScrollToComments = initiallyScrollToComments,
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
private fun rememberPostId(navArguments: Bundle): Long =
    remember(navArguments) {
        navArguments.getString("postId")!!.toLong()
    }

@Composable
private fun rememberInitiallyScrollToComments(navArguments: Bundle): Boolean =
    remember(navArguments) {
        navArguments.getString("initiallyScrollToComments").toBoolean()
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