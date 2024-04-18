package ru.ilyasekunov.officeapp.navigation

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
        navArguments.getLong("sda")
        val postId = remember(navArguments) { navArguments.getString("postId")!!.toLong() }
        val initiallyScrollToComments = remember(navArguments) {
            navArguments.getString("initiallyScrollToComments").toBoolean()
        }

        val ideaDetailsViewModel = hiltViewModel<IdeaDetailsViewModel>()
        LaunchedEffect(Unit) {
            ideaDetailsViewModel.loadPostById(postId)
            ideaDetailsViewModel.loadCommentsByPostId(postId)
        }

        val comments = ideaDetailsViewModel.commentsUiState.collectAsLazyPagingItems()
        IdeaDetailsScreen(
            ideaPostUiState = ideaDetailsViewModel.ideaPostUiState,
            sendingMessageUiState = ideaDetailsViewModel.sendingMessageUiState,
            currentCommentsSortingFilter = ideaDetailsViewModel.currentCommentsFilter,
            onCommentsFilterSelect = ideaDetailsViewModel::updateCommentsFilterUiState,
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