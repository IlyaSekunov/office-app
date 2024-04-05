package ru.ilyasekunov.officeapp.ui.home.ideadetails

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.data.model.Comment
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.ui.ErrorScreen
import ru.ilyasekunov.officeapp.ui.LoadingScreen
import ru.ilyasekunov.officeapp.ui.comments.comments
import ru.ilyasekunov.officeapp.ui.components.AsyncImageWithLoading
import ru.ilyasekunov.officeapp.ui.components.BasicPullToRefreshContainer
import ru.ilyasekunov.officeapp.ui.components.LikesAndDislikesSection
import ru.ilyasekunov.officeapp.ui.components.NavigateBackArrow
import ru.ilyasekunov.officeapp.ui.components.SendingMessageBottomBar
import ru.ilyasekunov.officeapp.ui.components.SendingMessageUiState
import ru.ilyasekunov.officeapp.ui.components.defaultNavigateBackArrowScrollBehaviour
import ru.ilyasekunov.officeapp.ui.home.AttachedImages
import ru.ilyasekunov.officeapp.ui.home.editidea.AttachedImage
import ru.ilyasekunov.officeapp.ui.networkErrorSnackbar
import ru.ilyasekunov.officeapp.util.toRussianString
import java.time.LocalDateTime

private object IdeaDetailsScreenDefaults {
    const val COMMENTS_SECTION_OFFSET_INDEX = 1
}

@Composable
fun IdeaDetailsScreen(
    ideaPostUiState: IdeaPostUiState,
    sendingMessageUiState: SendingMessageUiState,
    onRetryPostLoad: () -> Unit,
    onRetryCommentsLoad: () -> Unit,
    onPullToRefresh: suspend () -> Unit,
    comments: LazyPagingItems<Comment>,
    onCommentLikeClick: (Comment) -> Unit,
    onCommentDislikeClick: (Comment) -> Unit,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    onAttachImage: (Uri) -> Unit,
    onRemoveImageClick: (AttachedImage) -> Unit,
    onSendCommentClick: () -> Unit,
    onMessageValueChange: (String) -> Unit,
    initiallyScrollToComments: Boolean = false,
    navigateToIdeaAuthorScreen: (authorId: Long) -> Unit,
    navigateBack: () -> Unit
) {
    when {
        !ideaPostUiState.postExists -> PostsNotExists(navigateBack)
        ideaPostUiState.isLoading || ideaPostUiState.ideaPost == null -> LoadingScreen()
        sendingMessageUiState.isLoading -> LoadingScreen()
        ideaPostUiState.isErrorWhileLoading -> {
            ErrorScreen(
                message = stringResource(R.string.error_connecting_to_server),
                onRetryButtonClick = onRetryPostLoad
            )
        }

        else -> {
            val snackbarHostState = remember { SnackbarHostState() }
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                bottomBar = {
                    SendingMessageBottomBar(
                        sendingMessageUiState = sendingMessageUiState,
                        onMessageValueChange = onMessageValueChange,
                        onSendClick = onSendCommentClick,
                        onImageRemoveClick = onRemoveImageClick,
                        onImageAttach = onAttachImage,
                        containerColor = MaterialTheme.colorScheme.background,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .imePadding()
                    )
                }
            ) { paddingValues ->
                IdeaDetailsScreenContent(
                    ideaPost = ideaPostUiState.ideaPost,
                    onRetryCommentsLoad = onRetryCommentsLoad,
                    onPullToRefresh = onPullToRefresh,
                    comments = comments,
                    onCommentLikeClick = onCommentLikeClick,
                    onCommentDislikeClick = onCommentDislikeClick,
                    onLikeClick = onLikeClick,
                    onDislikeClick = onDislikeClick,
                    navigateToIdeaAuthorScreen = navigateToIdeaAuthorScreen,
                    navigateBack = navigateBack,
                    initiallyScrollToComments = initiallyScrollToComments,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            ObserveIsErrorWhileSendingComment(
                sendingMessageUiState = sendingMessageUiState,
                snackbarHostState = snackbarHostState,
                onRetryButtonClick = onSendCommentClick
            )
        }
    }
}

@Composable
private fun IdeaDetailsScreenContent(
    ideaPost: IdeaPost,
    onRetryCommentsLoad: () -> Unit,
    onPullToRefresh: suspend () -> Unit,
    comments: LazyPagingItems<Comment>,
    onCommentLikeClick: (Comment) -> Unit,
    onCommentDislikeClick: (Comment) -> Unit,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    navigateToIdeaAuthorScreen: (authorId: Long) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    initiallyScrollToComments: Boolean = false
) {
    BasicPullToRefreshContainer(
        onRefreshTrigger = onPullToRefresh,
        modifier = modifier
    ) {
        val navigateBackArrowScrollBehaviour = defaultNavigateBackArrowScrollBehaviour()
        LazyColumn(
            state = rememberIdeaDetailsScrollState(initiallyScrollToComments),
            contentPadding = PaddingValues(top = 48.dp, bottom = 14.dp),
            modifier = Modifier.nestedScroll(
                connection = navigateBackArrowScrollBehaviour.nestedScrollConnection
            )
        ) {
            ideaDetailsSection(
                ideaPost = ideaPost,
                onLikeClick = onLikeClick,
                onDislikeClick = onDislikeClick,
                navigateToIdeaAuthorScreen = navigateToIdeaAuthorScreen,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            comments(
                comments = comments,
                onRetryCommentsLoad = onRetryCommentsLoad,
                onCommentLikeClick = onCommentLikeClick,
                onCommentDislikeClick = onCommentDislikeClick,
                navigateToIdeaAuthorScreen = navigateToIdeaAuthorScreen
            )
        }
        NavigateBackArrow(
            onClick = navigateBack,
            scrollBehaviour = navigateBackArrowScrollBehaviour,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 10.dp)
        )
    }
}

private fun LazyListScope.ideaDetailsSection(
    ideaPost: IdeaPost,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    navigateToIdeaAuthorScreen: (authorId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    item {
        Column(modifier = modifier) {
            AuthorInfoSection(
                ideaAuthor = ideaPost.ideaAuthor,
                date = ideaPost.date,
                onClick = {
                    navigateToIdeaAuthorScreen(ideaPost.ideaAuthor.id)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp)
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            )
            IdeaPostDetailSection(ideaPost = ideaPost)
            Spacer(modifier = Modifier.height(18.dp))
            LikesAndDislikesSection(
                isLikePressed = ideaPost.isLikePressed,
                likesCount = ideaPost.likesCount,
                onLikeClick = onLikeClick,
                isDislikePressed = ideaPost.isDislikePressed,
                dislikesCount = ideaPost.dislikesCount,
                onDislikeClick = onDislikeClick,
                likesIconSize = 16.dp,
                dislikesIconSize = 16.dp,
                textSize = 14.sp,
                spaceBetweenCategories = 15.dp,
                buttonsWithBackground = true,
                buttonsWithRippleEffect = true,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
    }
}

@Composable
private fun ObserveIsErrorWhileSendingComment(
    sendingMessageUiState: SendingMessageUiState,
    snackbarHostState: SnackbarHostState,
    onRetryButtonClick: () -> Unit
) {
    val errorMessage = stringResource(R.string.error_while_publishing_comment)
    val retryLabel = stringResource(R.string.retry)
    LaunchedEffect(sendingMessageUiState.isErrorWhileSending) {
        if (sendingMessageUiState.isErrorWhileSending) {
            networkErrorSnackbar(
                snackbarHostState = snackbarHostState,
                duration = SnackbarDuration.Short,
                message = errorMessage,
                retryLabel = retryLabel,
                onRetryClick = onRetryButtonClick
            )
        }
    }
}

@Composable
fun PostsNotExists(navigateBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        Text(
            text = stringResource(R.string.posts_not_exists),
            fontSize = 16.sp,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.align(Alignment.Center)
        )
        NavigateBackArrow(
            onClick = navigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 10.dp, top = 10.dp)
        )
    }
}

@Composable
private fun IdeaPostDetailSection(
    ideaPost: IdeaPost,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = ideaPost.title,
            fontSize = 32.sp,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        if (ideaPost.attachedImages.isNotEmpty()) {
            AttachedImages(
                attachedImages = ideaPost.attachedImages,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f / 0.85f)
            )
        }
        Spacer(modifier = Modifier.height(25.dp))
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = ideaPost.content,
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
    }
}

@Composable
private fun AuthorInfoSection(
    ideaAuthor: IdeaAuthor,
    date: LocalDateTime,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        AsyncImageWithLoading(
            model = ideaAuthor.photo,
            modifier = Modifier
                .size(105.dp)
                .clip(CircleShape)
        )
        Column {
            Text(
                text = "${ideaAuthor.name} ${ideaAuthor.surname}",
                fontSize = 16.sp,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = ideaAuthor.job,
                fontSize = 14.sp,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = date.toRussianString(),
                fontSize = 12.sp,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
private fun rememberIdeaDetailsScrollState(initiallyScrollToComments: Boolean): LazyListState {
    val lazyListState = rememberLazyListState()
    if (initiallyScrollToComments) {
        LaunchedEffect(lazyListState) {
            lazyListState.animateScrollToItem(
                IdeaDetailsScreenDefaults.COMMENTS_SECTION_OFFSET_INDEX
            )
        }
    }
    return lazyListState
}