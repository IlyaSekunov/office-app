package ru.ilyasekunov.officeapp.ui.ideadetails

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.data.model.Comment
import ru.ilyasekunov.officeapp.data.model.CommentsSortingFilters
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.ui.AnimatedLoadingScreen
import ru.ilyasekunov.officeapp.ui.ErrorScreen
import ru.ilyasekunov.officeapp.ui.LocalCoroutineScope
import ru.ilyasekunov.officeapp.ui.LocalSnackbarHostState
import ru.ilyasekunov.officeapp.ui.comments.comments
import ru.ilyasekunov.officeapp.ui.components.AsyncImageWithLoading
import ru.ilyasekunov.officeapp.ui.components.AttachedImage
import ru.ilyasekunov.officeapp.ui.components.BothDirectedPullToRefreshContainer
import ru.ilyasekunov.officeapp.ui.components.LikesAndDislikesSection
import ru.ilyasekunov.officeapp.ui.components.NavigateBackArrow
import ru.ilyasekunov.officeapp.ui.components.Scrim
import ru.ilyasekunov.officeapp.ui.components.SendingMessageBottomBar
import ru.ilyasekunov.officeapp.ui.components.SendingMessageUiState
import ru.ilyasekunov.officeapp.ui.components.defaultNavigateBackArrowScrollBehaviour
import ru.ilyasekunov.officeapp.ui.components.rememberNavigateBackArrowState
import ru.ilyasekunov.officeapp.ui.home.AttachedImages
import ru.ilyasekunov.officeapp.ui.home.CurrentUserUiState
import ru.ilyasekunov.officeapp.ui.snackbarWithAction
import ru.ilyasekunov.officeapp.util.toRussianString
import java.time.LocalDateTime

private object IdeaDetailsScreenDefaults {
    const val COMMENTS_SECTION_OFFSET_INDEX = 5
}

@Composable
fun IdeaDetailsScreen(
    currentUserUiState: CurrentUserUiState,
    ideaPostUiState: IdeaPostUiState,
    sendingMessageUiState: SendingMessageUiState,
    currentCommentsSortingFilter: CommentsSortingFilters,
    comments: LazyPagingItems<Comment>,
    currentEditableComment: Comment?,
    commentDeletingUiState: CommentDeletingUiState?,
    initiallyScrollToComments: Boolean = false,
    onRetryInfoLoad: () -> Unit,
    onRetryCommentsLoad: () -> Unit,
    onPullToRefresh: CoroutineScope.() -> Job,
    onCommentLikeClick: (Comment) -> Unit,
    onCommentDislikeClick: (Comment) -> Unit,
    onEditCommentClick: (Comment) -> Unit,
    onEditCommentDismiss: () -> Unit,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    onAttachImage: (Uri) -> Unit,
    onRemoveImageClick: (AttachedImage) -> Unit,
    onSendCommentClick: () -> Unit,
    onDeleteCommentClick: (Comment) -> Unit,
    onMessageValueChange: (String) -> Unit,
    onCommentsFilterSelect: (CommentsSortingFilters) -> Unit,
    navigateToIdeaAuthorScreen: (authorId: Long) -> Unit,
    navigateBack: () -> Unit
) {
    when {
        isErrorWhileLoading(ideaPostUiState, currentUserUiState) -> {
            ErrorScreen(
                message = stringResource(R.string.error_connecting_to_server),
                onRetryButtonClick = onRetryInfoLoad
            )
        }

        !ideaPostUiState.postExists -> PostsNotExists(navigateBack)
        isScreenLoading(
            currentUserUiState,
            ideaPostUiState,
            sendingMessageUiState,
            commentDeletingUiState
        ) -> AnimatedLoadingScreen()

        else -> {
            val snackbarHostState = LocalSnackbarHostState.current
            val coroutineScope = LocalCoroutineScope.current
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                bottomBar = {
                    SendingMessageBottomBarWithEditOption(
                        sendingMessageUiState = sendingMessageUiState,
                        currentEditableComment = currentEditableComment,
                        onMessageValueChange = onMessageValueChange,
                        onSendClick = onSendCommentClick,
                        onImageRemoveClick = onRemoveImageClick,
                        onImageAttach = onAttachImage,
                        containerColor = MaterialTheme.colorScheme.background,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .imePadding()
                    )
                },
                modifier = Modifier.fillMaxSize()
            ) { paddingValues ->
                IdeaDetailsScreenContent(
                    ideaPost = ideaPostUiState.ideaPost!!,
                    currentCommentsSortingFilter = currentCommentsSortingFilter,
                    currentEditableComment = currentEditableComment,
                    comments = comments,
                    initiallyScrollToComments = initiallyScrollToComments,
                    isCommentOwnerCurrentUser = {
                        it.author.id == currentUserUiState.user?.id
                    },
                    onFilterClick = onCommentsFilterSelect,
                    onRetryCommentsLoad = onRetryCommentsLoad,
                    onPullToRefresh = onPullToRefresh,
                    onCommentLikeClick = onCommentLikeClick,
                    onCommentDislikeClick = onCommentDislikeClick,
                    onEditCommentClick = onEditCommentClick,
                    onEditCommentDismiss = onEditCommentDismiss,
                    onDeleteCommentClick = onDeleteCommentClick,
                    onLikeClick = onLikeClick,
                    onDislikeClick = onDislikeClick,
                    navigateToIdeaAuthorScreen = navigateToIdeaAuthorScreen,
                    navigateBack = navigateBack,

                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            ObserveStateChanges(
                sendingMessageUiState = sendingMessageUiState,
                commentDeletingUiState = commentDeletingUiState,
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope,
                onRetryCommentSend = onSendCommentClick
            )
        }
    }
}

@Composable
fun SendingMessageBottomBarWithEditOption(
    sendingMessageUiState: SendingMessageUiState,
    currentEditableComment: Comment?,
    onMessageValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onImageRemoveClick: (attachedImage: AttachedImage) -> Unit,
    onImageAttach: (Uri) -> Unit,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    SendingMessageBottomBar(
        sendingMessageUiState = sendingMessageUiState,
        onMessageValueChange = onMessageValueChange,
        onSendClick = onSendClick,
        onImageRemoveClick = onImageRemoveClick,
        onImageAttach = onImageAttach,
        containerColor = containerColor,
        modifier = modifier.focusRequester(focusRequester)
    )
    LaunchedEffect(currentEditableComment) {
        if (currentEditableComment != null) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
private fun IdeaDetailsScreenContent(
    ideaPost: IdeaPost,
    currentCommentsSortingFilter: CommentsSortingFilters,
    currentEditableComment: Comment?,
    comments: LazyPagingItems<Comment>,
    isCommentOwnerCurrentUser: (Comment) -> Boolean,
    onFilterClick: (CommentsSortingFilters) -> Unit,
    onRetryCommentsLoad: () -> Unit,
    onPullToRefresh: CoroutineScope.() -> Job,
    onEditCommentClick: (Comment) -> Unit,
    onEditCommentDismiss: () -> Unit,
    onDeleteCommentClick: (Comment) -> Unit,
    onCommentLikeClick: (Comment) -> Unit,
    onCommentDislikeClick: (Comment) -> Unit,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    navigateToIdeaAuthorScreen: (authorId: Long) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    initiallyScrollToComments: Boolean = false
) {
    val navigateBackArrowScrollBehaviour = defaultNavigateBackArrowScrollBehaviour(
        state = rememberNavigateBackArrowState(isVisible = !initiallyScrollToComments)
    )
    val topPadding = 48.dp
    BothDirectedPullToRefreshContainer(
        onRefreshTrigger = onPullToRefresh,
        modifier = modifier.nestedScroll(navigateBackArrowScrollBehaviour.nestedScrollConnection)
    ) { isRefreshing ->
        var contextSelectedComment by remember { mutableStateOf<Comment?>(null) }
        LazyColumn(
            state = rememberIdeaDetailsScrollState(
                initiallyScrollToComments = initiallyScrollToComments,
                scrollOffset = with(LocalDensity.current) { topPadding.toPx() }.toInt()
            ),
            contentPadding = PaddingValues(top = topPadding, bottom = 14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            authorInfoSection(
                ideaPost = ideaPost,
                navigateToIdeaAuthorScreen = navigateToIdeaAuthorScreen,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp)
            )
            item {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(10.dp)
                )
            }
            ideaPostDetailSection(
                ideaPost = ideaPost,
                modifier = Modifier.padding(bottom = 18.dp)
            )
            commentsAndDislikeSection(
                ideaPost = ideaPost,
                onLikeClick = onLikeClick,
                onDislikeClick = onDislikeClick,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            item {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 16.dp)
                )
            }
            commentsInfoSection(
                commentsCount = ideaPost.commentsCount,
                currentSortingFilter = currentCommentsSortingFilter,
                onFilterClick = onFilterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            comments(
                comments = comments,
                onCommentClick = {
                    if (isCommentOwnerCurrentUser(it)) {
                        contextSelectedComment = it
                    }
                },
                isPullToRefreshActive = isRefreshing,
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
        SelectedCommentDropdownMenu(
            expanded = contextSelectedComment != null,
            onDismiss = { contextSelectedComment = null },
            onEditClick = {
                onEditCommentClick(contextSelectedComment!!)
                contextSelectedComment = null
            },
            onDeleteClick = {
                onDeleteCommentClick(contextSelectedComment!!)
                contextSelectedComment = null
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
        if (currentEditableComment != null) {
            Scrim(
                onClick = onEditCommentDismiss,
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
            )
        }
    }
}

private fun LazyListScope.ideaPostDetailSection(
    ideaPost: IdeaPost,
    modifier: Modifier = Modifier
) {
    item {
        IdeaPostDetailSection(
            ideaPost = ideaPost,
            modifier = modifier
        )
    }
}

private fun LazyListScope.commentsAndDislikeSection(
    ideaPost: IdeaPost,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    item {
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
            modifier = modifier
        )
    }
}

private fun LazyListScope.authorInfoSection(
    ideaPost: IdeaPost,
    navigateToIdeaAuthorScreen: (authorId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    item {
        AuthorInfoSection(
            ideaAuthor = ideaPost.ideaAuthor,
            date = ideaPost.date,
            onClick = { navigateToIdeaAuthorScreen(ideaPost.ideaAuthor.id) },
            modifier = modifier
        )
    }
}

private fun LazyListScope.commentsInfoSection(
    commentsCount: Int,
    currentSortingFilter: CommentsSortingFilters,
    onFilterClick: (CommentsSortingFilters) -> Unit,
    modifier: Modifier = Modifier
) {
    item {
        Box(modifier = modifier) {
            var isMenuVisible by remember { mutableStateOf(false) }
            CommentsCountAndSortingFilter(
                commentsCount = commentsCount,
                currentSortingFilter = currentSortingFilter,
                onSortingFilterClick = { isMenuVisible = true },
                modifier = Modifier.fillMaxWidth()
            )
            CommentsFiltersDropdownMenu(
                currentCommentsFilter = currentSortingFilter,
                onFilterClick = {
                    onFilterClick(it)
                    isMenuVisible = false
                },
                expanded = isMenuVisible,
                onDismissClick = { isMenuVisible = false },
                shape = MaterialTheme.shapes.medium,
                containerColor = MaterialTheme.colorScheme.background,
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                menuItemPaddings = PaddingValues(13.dp),
                borderStroke = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
private fun CommentsCountAndSortingFilter(
    commentsCount: Int,
    currentSortingFilter: CommentsSortingFilters,
    onSortingFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        Text(
            text = commentsCount.toRussianCommentsString(),
            fontSize = 13.sp,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onSortingFilterClick
                )
        ) {
            Text(
                text = stringCommentsFilter(currentSortingFilter),
                fontSize = 15.sp,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                painter = painterResource(R.drawable.baseline_keyboard_arrow_down_24),
                contentDescription = "arrow_down",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ObserveStateChanges(
    sendingMessageUiState: SendingMessageUiState,
    commentDeletingUiState: CommentDeletingUiState?,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onRetryCommentSend: () -> Unit
) {
    ObserveCommentIsPublished(
        sendingMessageUiState = sendingMessageUiState,
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope
    )
    ObserveIsErrorWhileSendingComment(
        sendingMessageUiState = sendingMessageUiState,
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope,
        onRetryButtonClick = onRetryCommentSend
    )
    ObserveIsCommentWasDeletedSuccessfully(
        commentDeletingUiState = commentDeletingUiState,
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope
    )
}

@Composable
fun ObserveIsCommentWasDeletedSuccessfully(
    commentDeletingUiState: CommentDeletingUiState?,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    commentDeletingUiState?.let {
        val message = if (it.isDeleted) {
            stringResource(R.string.comment_delete_success)
        } else {
            stringResource(R.string.comment_delete_failure)
        }
        LaunchedEffect(commentDeletingUiState) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
}

@Composable
private fun ObserveCommentIsPublished(
    sendingMessageUiState: SendingMessageUiState,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    val message = stringResource(R.string.comment_published_successfully)
    LaunchedEffect(sendingMessageUiState) {
        if (sendingMessageUiState.isPublished) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
}

@Composable
private fun ObserveIsErrorWhileSendingComment(
    sendingMessageUiState: SendingMessageUiState,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onRetryButtonClick: () -> Unit
) {
    val errorMessage = stringResource(R.string.error_while_publishing_comment)
    val retryLabel = stringResource(R.string.retry)
    LaunchedEffect(sendingMessageUiState) {
        if (sendingMessageUiState.isErrorWhileSending) {
            coroutineScope.launch {
                snackbarHostState.snackbarWithAction(
                    message = errorMessage,
                    actionLabel = retryLabel,
                    onActionClick = onRetryButtonClick,
                    duration = SnackbarDuration.Short
                )
            }
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
            modifier = Modifier.padding(
                start = 10.dp,
                end = 10.dp,
                top = 10.dp,
                bottom = 20.dp
            )
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
private fun rememberIdeaDetailsScrollState(
    initiallyScrollToComments: Boolean,
    scrollOffset: Int
): LazyListState {
    val lazyListState = rememberLazyListState()
    if (initiallyScrollToComments) {
        LaunchedEffect(lazyListState) {
            lazyListState.animateScrollToItem(
                IdeaDetailsScreenDefaults.COMMENTS_SECTION_OFFSET_INDEX,
                scrollOffset
            )
        }
    }
    return lazyListState
}

@Composable
fun stringCommentsFilter(commentsFilter: CommentsSortingFilters) =
    when (commentsFilter) {
        CommentsSortingFilters.NEW -> stringResource(R.string.comments_filter_new)
        CommentsSortingFilters.OLD -> stringResource(R.string.comments_filter_old)
        CommentsSortingFilters.POPULAR -> stringResource(R.string.comments_filter_popular)
        CommentsSortingFilters.UNPOPULAR -> stringResource(R.string.comments_filter_unpopular)
    }

fun Int.toRussianCommentsString(): String =
    when (this % 10) {
        0, 5, 6, 7, 8, 9 -> "$this КОММЕНТАРИЕВ"
        1 -> "$this КОММЕНТАРИЙ"
        2, 3, 4 -> "$this КОММЕНТАРИЯ"
        else -> throw IllegalArgumentException()
    }

private fun isScreenLoading(
    currentUserUiState: CurrentUserUiState,
    ideaPostUiState: IdeaPostUiState,
    sendingMessageUiState: SendingMessageUiState,
    commentDeletingUiState: CommentDeletingUiState?
): Boolean {
    val commentIsDeleting = commentDeletingUiState?.isDeleting ?: false
    return commentIsDeleting ||
            sendingMessageUiState.isLoading ||
            ideaPostUiState.isLoading ||
            currentUserUiState.isLoading
}

private fun isErrorWhileLoading(
    ideaPostUiState: IdeaPostUiState,
    currentUserUiState: CurrentUserUiState
) = ideaPostUiState.isErrorWhileLoading || currentUserUiState.isErrorWhileLoading