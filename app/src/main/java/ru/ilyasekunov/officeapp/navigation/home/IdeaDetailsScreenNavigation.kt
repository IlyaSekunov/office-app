package ru.ilyasekunov.officeapp.navigation.home

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import ru.ilyasekunov.officeapp.navigation.Screen
import ru.ilyasekunov.officeapp.ui.animations.enterSlideLeft
import ru.ilyasekunov.officeapp.ui.animations.exitSlideRight
import ru.ilyasekunov.officeapp.ui.home.ideadetails.IdeaDetailsScreen
import ru.ilyasekunov.officeapp.ui.home.ideadetails.IdeaDetailsViewModel

fun NavGraphBuilder.ideaDetailsScreen(
    navigateToIdeaAuthorScreen: (authorId: Long) -> Unit,
    navigateBack: () -> Unit
) {
    composable(
        route = Screen.IdeaDetails.route,
        enterTransition = { enterSlideLeft() },
        exitTransition = { exitSlideRight() }
    ) { backStackEntry ->
        val navArguments = backStackEntry.arguments!!
        val postId = navArguments.getLong("postId")
        val initiallyScrollToComments = navArguments.getString("initiallyScrollToComments")

        val ideaDetailsViewModel = hiltViewModel<IdeaDetailsViewModel>()
        LaunchedEffect(Unit) {
            ideaDetailsViewModel.loadPostById(postId)
            ideaDetailsViewModel.loadCommentsByPostId(postId)
        }

        val comments = ideaDetailsViewModel.commentsUiState.collectAsLazyPagingItems()
        IdeaDetailsScreen(
            ideaPostUiState = ideaDetailsViewModel.ideaPostUiState,
            sendingMessageUiState = ideaDetailsViewModel.sendingMessageUiState,
            onRetryPostLoad = { ideaDetailsViewModel.loadPostById(postId) },
            onRetryCommentsLoad = { ideaDetailsViewModel.loadCommentsByPostId(postId) },
            onPullToRefresh = {
                ideaDetailsViewModel.loadPostByIdSuspending(postId)
                comments.refresh()
            },
            comments = comments,
            onCommentLikeClick = ideaDetailsViewModel::updateCommentLike,
            onCommentDislikeClick = ideaDetailsViewModel::updateCommentDislike,
            onLikeClick = ideaDetailsViewModel::updatePostLike,
            onDislikeClick = ideaDetailsViewModel::updatePostDislike,
            onMessageValueChange = ideaDetailsViewModel::updateMessage,
            onAttachImage = ideaDetailsViewModel::attachImage,
            onRemoveImageClick = ideaDetailsViewModel::removeImage,
            onSendCommentClick = ideaDetailsViewModel::sendComment,
            initiallyScrollToComments = initiallyScrollToComments.toBoolean(),
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
        .replace("{postId}", postId.toString())
        .replace("{initiallyScrollToComments}", initiallyScrollToComments.toString())
    navigate(destination, navOptions)
}