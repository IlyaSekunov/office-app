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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.CoroutineScope
import ru.ilyasekunov.officeapp.LocalCoroutineScope
import ru.ilyasekunov.officeapp.LocalSnackbarHostState
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.data.model.Comment
import ru.ilyasekunov.officeapp.data.model.CommentsSortingFilters
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.ui.ErrorScreen
import ru.ilyasekunov.officeapp.ui.LoadingScreen
import ru.ilyasekunov.officeapp.ui.comments.comments
import ru.ilyasekunov.officeapp.ui.components.AsyncImageWithLoading
import ru.ilyasekunov.officeapp.ui.components.AttachedImage
import ru.ilyasekunov.officeapp.ui.components.BasicPullToRefreshContainer
import ru.ilyasekunov.officeapp.ui.components.LikesAndDislikesSection
import ru.ilyasekunov.officeapp.ui.components.NavigateBackArrow
import ru.ilyasekunov.officeapp.ui.components.SendingMessageBottomBar
import ru.ilyasekunov.officeapp.ui.components.SendingMessageUiState
import ru.ilyasekunov.officeapp.ui.components.defaultNavigateBackArrowScrollBehaviour
import ru.ilyasekunov.officeapp.ui.components.rememberNavigateBackArrowState
import ru.ilyasekunov.officeapp.ui.home.AttachedImages
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
    currentCommentsSortingFilter: CommentsSortingFilters,
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
    onCommentsFilterSelect: (CommentsSortingFilters) -> Unit,
    initiallyScrollToComments: Boolean = false,
    navigateToIdeaAuthorScreen: (authorId: Long) -> Unit,
    navigateBack: () -> Unit
) {
    when {
        ideaPostUiState.isErrorWhileLoading -> {
            ErrorScreen(
                message = stringResource(R.string.error_connecting_to_server),
                onRetryButtonClick = onRetryPostLoad
            )
        }

        !ideaPostUiState.postExists -> PostsNotExists(navigateBack)
        ideaPostUiState.isLoading || ideaPostUiState.ideaPost == null -> LoadingScreen()
        sendingMessageUiState.isLoading -> LoadingScreen()

        else -> {
            val snackbarHostState = LocalSnackbarHostState.current
            val coroutineScope = LocalCoroutineScope.current
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
                },
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) { paddingValues ->
                IdeaDetailsScreenContent(
                    ideaPost = ideaPostUiState.ideaPost,
                    currentCommentsSortingFilter = currentCommentsSortingFilter,
                    onFilterClick = onCommentsFilterSelect,
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            ObserveIsErrorWhileSendingComment(
                sendingMessageUiState = sendingMessageUiState,
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope,
                onRetryButtonClick = onSendCommentClick
            )
        }
    }
}

@Composable
private fun IdeaDetailsScreenContent(
    ideaPost: IdeaPost,
    currentCommentsSortingFilter: CommentsSortingFilters,
    onFilterClick: (CommentsSortingFilters) -> Unit,
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
        onRefreshTrigger = onPullToRefresh
    ) {
        val navigateBackArrowScrollBehaviour = defaultNavigateBackArrowScrollBehaviour(
            state = rememberNavigateBackArrowState(
                isVisible = !initiallyScrollToComments
            )
        )
        val topPadding = 48.dp
        LazyColumn(
            state = rememberIdeaDetailsScrollState(
                initiallyScrollToComments = initiallyScrollToComments,
                scrollOffset = (topPadding.value * LocalDensity.current.density).toInt()
            ),
            contentPadding = PaddingValues(top = topPadding, bottom = 14.dp),
            modifier = modifier
                .nestedScroll(
                    connection = navigateBackArrowScrollBehaviour.nestedScrollConnection
                )
        ) {
            ideaDetailsSection(
                ideaPost = ideaPost,
                onLikeClick = onLikeClick,
                onDislikeClick = onDislikeClick,
                navigateToIdeaAuthorScreen = navigateToIdeaAuthorScreen
            )
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
private fun ObserveIsErrorWhileSendingComment(
    sendingMessageUiState: SendingMessageUiState,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onRetryButtonClick: () -> Unit
) {
    val errorMessage = stringResource(R.string.error_while_publishing_comment)
    val retryLabel = stringResource(R.string.retry)
    LaunchedEffect(sendingMessageUiState.isErrorWhileSending) {
        if (sendingMessageUiState.isErrorWhileSending) {
            networkErrorSnackbar(
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope,
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