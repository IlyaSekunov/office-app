package ru.ilyasekunov.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import ru.ilyasekunov.filters.FiltersUiState
import ru.ilyasekunov.filters.OfficeFilterUiState
import ru.ilyasekunov.filters.sortingCategoryName
import ru.ilyasekunov.model.IdeaAuthor
import ru.ilyasekunov.model.IdeaPost
import ru.ilyasekunov.model.Office
import ru.ilyasekunov.model.SortingCategory
import ru.ilyasekunov.profile.CurrentUserUiState
import ru.ilyasekunov.ui.AnimatedLoadingScreen
import ru.ilyasekunov.ui.ErrorScreen
import ru.ilyasekunov.ui.LocalCoroutineScope
import ru.ilyasekunov.ui.LocalCurrentNavigationBarScreen
import ru.ilyasekunov.ui.LocalSnackbarHostState
import ru.ilyasekunov.ui.RetryButton
import ru.ilyasekunov.ui.components.AsyncImageWithLoading
import ru.ilyasekunov.ui.components.BothDirectedPullToRefreshContainer
import ru.ilyasekunov.ui.components.BottomNavigationBar
import ru.ilyasekunov.ui.components.LazyPagingItemsColumn
import ru.ilyasekunov.ui.components.LikesAndDislikesSection
import ru.ilyasekunov.ui.components.SuggestIdeaButton
import ru.ilyasekunov.ui.components.defaultSuggestIdeaFABScrollBehaviour
import ru.ilyasekunov.ui.modifiers.shadow
import ru.ilyasekunov.ui.snackbarWithAction
import ru.ilyasekunov.ui.theme.OfficeAppTheme
import ru.ilyasekunov.util.toRussianString
import ru.ilyasekunov.util.toThousandsString
import java.time.LocalDateTime
import ru.ilyasekunov.officeapp.core.ui.R as coreUiResources
import ru.ilyasekunov.officeapp.feature.filters.R as featureFiltersResources

@Composable
fun HomeScreen(
    posts: LazyPagingItems<IdeaPost>,
    currentUserUiState: CurrentUserUiState,
    searchUiState: SearchUiState,
    filtersUiState: FiltersUiState,
    suggestIdeaToMyOfficeUiState: SuggestIdeaToMyOfficeUiState,
    deletePostUiState: DeletePostUiState,
    postsLazyListState: LazyListState,
    onSearchValueChange: (String) -> Unit,
    onOfficeFilterRemoveClick: (OfficeFilterUiState) -> Unit,
    onSortingFilterRemoveClick: () -> Unit,
    onDeletePostClick: (IdeaPost) -> Unit,
    onDeletePostResultShown: () -> Unit,
    onPostLikeClick: (IdeaPost) -> Unit,
    onPostDislikeClick: (IdeaPost) -> Unit,
    onRetryInfoLoad: () -> Unit,
    onPullToRefresh: CoroutineScope.() -> Job,
    onSuggestIdeaToMyOfficeClick: (IdeaPost) -> Unit,
    onSuggestIdeaToMyOfficeResultShown: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToSuggestIdeaScreen: () -> Unit,
    navigateToFiltersScreen: () -> Unit,
    navigateToIdeaDetailsScreen: (postId: Long, initiallyScrollToComments: Boolean) -> Unit,
    navigateToAuthorScreen: (authorId: Long) -> Unit,
    navigateToEditIdeaScreen: (postId: Long) -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateToAuthGraph: () -> Unit
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val coroutineScope = LocalCoroutineScope.current
    val suggestIdeaFABScrollBehaviour = defaultSuggestIdeaFABScrollBehaviour()

    Scaffold(
        topBar = {
            HomeAppBar(
                searchUiState = searchUiState,
                onSearchValueChange = onSearchValueChange,
                onFiltersClick = navigateToFiltersScreen,
                filtersUiState = filtersUiState,
                onOfficeFilterRemoveClick = onOfficeFilterRemoveClick,
                onSortingFilterRemoveClick = onSortingFilterRemoveClick,
                modifier = Modifier.fillMaxWidth()
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = LocalCurrentNavigationBarScreen.current,
                navigateToHomeScreen = navigateToHomeScreen,
                navigateToFavouriteScreen = navigateToFavouriteScreen,
                navigateToMyOfficeScreen = navigateToMyOfficeScreen,
                navigateToProfileScreen = navigateToProfileScreen,
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            SuggestIdeaButton(
                onClick = navigateToSuggestIdeaScreen,
                scrollBehaviour = suggestIdeaFABScrollBehaviour
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        when {
            currentUserUiState.isUnauthorized -> navigateToAuthGraph()
            isScreenLoading(
                currentUserUiState,
                filtersUiState,
                suggestIdeaToMyOfficeUiState,
                deletePostUiState
            ) -> AnimatedLoadingScreen()

            isErrorWhileLoading(currentUserUiState, filtersUiState) -> {
                ErrorScreen(
                    message = stringResource(coreUiResources.string.core_ui_error_connecting_to_server),
                    onRetryButtonClick = onRetryInfoLoad
                )
            }

            else -> {
                HomeScreenContent(
                    posts = posts,
                    postsLazyListState = postsLazyListState,
                    currentUserUiState = currentUserUiState,
                    onDeletePostClick = onDeletePostClick,
                    onPostLikeClick = onPostLikeClick,
                    onPostDislikeClick = onPostDislikeClick,
                    onPullToRefresh = onPullToRefresh,
                    onSuggestIdeaToMyOfficeClick = onSuggestIdeaToMyOfficeClick,
                    navigateToIdeaDetailsScreen = navigateToIdeaDetailsScreen,
                    navigateToAuthorScreen = navigateToAuthorScreen,
                    navigateToEditIdeaScreen = navigateToEditIdeaScreen,
                    snackbarHostState = snackbarHostState,
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .nestedScroll(suggestIdeaFABScrollBehaviour.nestedScrollConnection)
                )
                ObserveSuggestIdeaToMyOfficeState(
                    suggestIdeaToMyOfficeUiState = suggestIdeaToMyOfficeUiState,
                    snackbarHostState = snackbarHostState,
                    coroutineScope = coroutineScope,
                    onResultShown = onSuggestIdeaToMyOfficeResultShown
                )
                ObserveDeletePostUiState(
                    deletePostUiState = deletePostUiState,
                    snackbarHostState = snackbarHostState,
                    coroutineScope = coroutineScope,
                    onResultShown = onDeletePostResultShown,
                    onDeleteSuccess = posts::refresh
                )
            }
        }
    }
}

@Composable
fun ObserveDeletePostUiState(
    deletePostUiState: DeletePostUiState,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onResultShown: () -> Unit,
    onDeleteSuccess: () -> Unit
) {
    ObserveDeletePostIsSuccess(
        deletePostUiState = deletePostUiState,
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope,
        onSuccessShown = onResultShown,
        onDeleteSuccess = onDeleteSuccess
    )
    ObserveDeletePostIsError(
        deletePostUiState = deletePostUiState,
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope,
        onErrorShown = onResultShown
    )
}

@Composable
fun ObserveDeletePostIsSuccess(
    deletePostUiState: DeletePostUiState,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onSuccessShown: () -> Unit,
    onDeleteSuccess: () -> Unit
) {
    val message = stringResource(coreUiResources.string.core_ui_delete_post_success)
    val currentOnSuccessShown by rememberUpdatedState(onSuccessShown)
    val currentOnDeleteSuccess by rememberUpdatedState(onDeleteSuccess)

    LaunchedEffect(Unit) {
        snapshotFlow { deletePostUiState }
            .filter { it.isSuccess }
            .collectLatest {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short
                    )
                    currentOnSuccessShown()
                }
                currentOnDeleteSuccess()
            }
    }
}

@Composable
fun ObserveDeletePostIsError(
    deletePostUiState: DeletePostUiState,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onErrorShown: () -> Unit
) {
    val message = stringResource(coreUiResources.string.core_ui_delete_post_error)
    val currentOnErrorShown by rememberUpdatedState(onErrorShown)

    LaunchedEffect(Unit) {
        snapshotFlow { deletePostUiState }
            .filter { it.isError }
            .collectLatest {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short
                    )
                    currentOnErrorShown()
                }
            }
    }
}

@Composable
private fun ObserveSuggestIdeaToMyOfficeState(
    suggestIdeaToMyOfficeUiState: SuggestIdeaToMyOfficeUiState,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onResultShown: () -> Unit
) {
    ObserveSuggestIdeaToMyOfficeIsError(
        suggestIdeaToMyOfficeUiState = suggestIdeaToMyOfficeUiState,
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope,
        onErrorShown = onResultShown
    )
    ObserveSuggestIdeaToMyOfficeIsSuccess(
        suggestIdeaToMyOfficeUiState = suggestIdeaToMyOfficeUiState,
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope,
        onSuccessShown = onResultShown
    )
}

@Composable
private fun ObserveSuggestIdeaToMyOfficeIsError(
    suggestIdeaToMyOfficeUiState: SuggestIdeaToMyOfficeUiState,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onErrorShown: () -> Unit
) {
    val message = stringResource(coreUiResources.string.core_ui_suggest_idea_to_my_office_failure)
    val currentOnErrorShown by rememberUpdatedState(onErrorShown)

    LaunchedEffect(Unit) {
        snapshotFlow { suggestIdeaToMyOfficeUiState }
            .filter { it.isError }
            .collectLatest {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short
                    )
                    currentOnErrorShown()
                }
            }
    }
}

@Composable
private fun ObserveSuggestIdeaToMyOfficeIsSuccess(
    suggestIdeaToMyOfficeUiState: SuggestIdeaToMyOfficeUiState,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onSuccessShown: () -> Unit
) {
    val message = stringResource(coreUiResources.string.core_ui_suggest_idea_to_my_office_success)
    val currentOnSuccessShown by rememberUpdatedState(onSuccessShown)

    LaunchedEffect(Unit) {
        snapshotFlow { suggestIdeaToMyOfficeUiState }
            .filter { it.isSuccess }
            .collectLatest {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short
                    )
                    currentOnSuccessShown()
                }
            }
    }
}

@Composable
private fun HomeScreenContent(
    posts: LazyPagingItems<IdeaPost>,
    currentUserUiState: CurrentUserUiState,
    onDeletePostClick: (IdeaPost) -> Unit,
    onPostLikeClick: (IdeaPost) -> Unit,
    onPostDislikeClick: (IdeaPost) -> Unit,
    onPullToRefresh: CoroutineScope.() -> Job,
    onSuggestIdeaToMyOfficeClick: (IdeaPost) -> Unit,
    navigateToIdeaDetailsScreen: (postId: Long, initiallyScrollToComments: Boolean) -> Unit,
    navigateToAuthorScreen: (authorId: Long) -> Unit,
    navigateToEditIdeaScreen: (postId: Long) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = LocalSnackbarHostState.current,
    coroutineScope: CoroutineScope = LocalCoroutineScope.current,
    postsLazyListState: LazyListState = rememberLazyListState()
) {
    BothDirectedPullToRefreshContainer(
        onRefreshTrigger = onPullToRefresh,
        modifier = modifier
    ) { isRefreshing ->
        val postDeletedMessage = stringResource(coreUiResources.string.core_ui_post_will_be_deleted_in_5_seconds)
        val undoLabel = stringResource(coreUiResources.string.core_ui_undo)

        HomeScreenIdeaPosts(
            posts = posts,
            isPullToRefreshActive = isRefreshing,
            lazyListState = postsLazyListState,
            isIdeaAuthorCurrentUser = { it.id == currentUserUiState.user!!.id },
            onDeletePostClick = {
                coroutineScope.launch {
                    snackbarHostState.snackbarWithAction(
                        message = postDeletedMessage,
                        actionLabel = undoLabel,
                        onTimeout = { onDeletePostClick(it) },
                        duration = SnackbarDuration.Short
                    )
                }
            },
            onPostLikeClick = onPostLikeClick,
            onPostDislikeClick = onPostDislikeClick,
            onSuggestIdeaToMyOfficeClick = onSuggestIdeaToMyOfficeClick,
            navigateToIdeaDetailsScreen = navigateToIdeaDetailsScreen,
            navigateToAuthorScreen = navigateToAuthorScreen,
            navigateToEditIdeaScreen = navigateToEditIdeaScreen,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun HomeScreenIdeaPosts(
    posts: LazyPagingItems<IdeaPost>,
    isPullToRefreshActive: Boolean,
    isIdeaAuthorCurrentUser: (IdeaAuthor) -> Boolean,
    onDeletePostClick: (IdeaPost) -> Unit,
    onPostLikeClick: (post: IdeaPost) -> Unit,
    onPostDislikeClick: (post: IdeaPost) -> Unit,
    onSuggestIdeaToMyOfficeClick: (IdeaPost) -> Unit,
    navigateToIdeaDetailsScreen: (postId: Long, initiallyScrollToComments: Boolean) -> Unit,
    navigateToAuthorScreen: (authorId: Long) -> Unit,
    navigateToEditIdeaScreen: (postId: Long) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState()
) {
    LazyPagingItemsColumn(
        items = posts,
        itemKey = { it.id },
        isPullToRefreshActive = isPullToRefreshActive,
        lazyListState = lazyListState,
        itemComposable = { post ->
            PostCard(
                post = post,
                isAuthorPostCurrentUser = isIdeaAuthorCurrentUser(post.ideaAuthor),
                onClick = { navigateToIdeaDetailsScreen(post.id, false) },
                onAuthorClick = { navigateToAuthorScreen(post.ideaAuthor.id) },
                onLikeClick = { onPostLikeClick(post) },
                onDislikeClick = { onPostDislikeClick(post) },
                onCommentsClick = { navigateToIdeaDetailsScreen(post.id, true) },
                onEditPostClick = { navigateToEditIdeaScreen(post.id) },
                onDeletePostClick = { onDeletePostClick(post) },
                onSuggestIdeaToMyOfficeClick = { onSuggestIdeaToMyOfficeClick(post) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        itemsEmptyComposable = { NoPostsAvailable() },
        errorWhileRefreshComposable = {
            ErrorScreen(
                message = stringResource(coreUiResources.string.core_ui_error_while_ideas_loading),
                onRetryButtonClick = posts::retry
            )
        },
        errorWhileAppendComposable = {
            ErrorWhileAppending(
                message = stringResource(coreUiResources.string.core_ui_error_while_ideas_loading),
                onRetryButtonClick = posts::retry,
                modifier = Modifier.padding(10.dp)
            )
        },
        contentPadding = PaddingValues(vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        appendIndicatorSize = 30.dp,
        modifier = modifier
    )
}

@Composable
private fun ErrorWhileAppending(
    message: String,
    onRetryButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = message,
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
        RetryButton(onClick = onRetryButtonClick)
    }
}

@Composable
fun HomeAppBar(
    searchUiState: SearchUiState,
    onSearchValueChange: (String) -> Unit,
    filtersUiState: FiltersUiState,
    onOfficeFilterRemoveClick: (OfficeFilterUiState) -> Unit,
    onSortingFilterRemoveClick: () -> Unit,
    onFiltersClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: (@Composable ColumnScope.(PaddingValues) -> Unit)? = null
) {
    val sortingFiltersUiState = filtersUiState.sortingFiltersUiState
    val officeFilterUiState = filtersUiState.officeFiltersUiState
    val showSortingFilter = sortingFiltersUiState.selected != null
    val showOfficeFilters = officeFilterUiState.any { it.isSelected }

    Column(
        modifier = modifier
            .shadow(blurRadius = 4.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 25.dp, start = 13.dp, end = 13.dp, bottom = 10.dp)
            .statusBarsPadding()
    ) {
        SearchTextField(
            searchValue = searchUiState.value,
            onSearchValueChange = onSearchValueChange,
            onFiltersClick = onFiltersClick
        )
        if (title != null) {
            title(PaddingValues(top = 20.dp, bottom = 10.dp))
        }
        if (showOfficeFilters) {
            AppliedOfficeFilters(
                officeFilters = officeFilterUiState,
                onOfficeFilterRemoveClick = onOfficeFilterRemoveClick,
                modifier = Modifier.padding(
                    top = if (title == null) 20.dp else 10.dp
                )
            )
        }
        if (showSortingFilter) {
            AppliedSortingFilter(
                sortingCategory = sortingFiltersUiState.selected!!,
                onSortingFilterRemoveClick = onSortingFilterRemoveClick,
                modifier = Modifier.padding(
                    top = if (showOfficeFilters) 30.dp else 10.dp
                )
            )
        }
    }
}

@Composable
private fun NoPostsAvailable(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(coreUiResources.string.core_ui_no_posts),
        style = MaterialTheme.typography.bodyLarge,
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .wrapContentSize(Alignment.Center)
    )
}

@Composable
fun PostCard(
    post: IdeaPost,
    isAuthorPostCurrentUser: Boolean,
    onClick: () -> Unit,
    onAuthorClick: () -> Unit,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    onCommentsClick: () -> Unit,
    onEditPostClick: () -> Unit,
    onDeletePostClick: () -> Unit,
    onSuggestIdeaToMyOfficeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    top = 16.dp,
                    bottom = 10.dp,
                    end = 20.dp
                )
        ) {
            IdeaPostAuthor(
                ideaAuthor = post.ideaAuthor,
                postDate = post.date,
                onClick = onAuthorClick,
                modifier = Modifier.clickable(onClick = onAuthorClick)
            )
            MenuSection(
                isAuthorPostCurrentUser = isAuthorPostCurrentUser,
                isIdeaSuggestedToMyOffice = post.isSuggestedToMyOffice,
                navigateToAuthorScreen = onAuthorClick,
                navigateToEditIdeaScreen = onEditPostClick,
                onDeletePostClick = onDeletePostClick,
                onSuggestIdeaToMyOfficeClick = onSuggestIdeaToMyOfficeClick
            )
        }
        Text(
            text = post.title,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 20.dp, bottom = 25.dp)
        )
        Text(
            text = post.content,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Justify,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(max = 85.dp)
        )
        if (post.attachedImages.isNotEmpty()) {
            AttachedImages(
                attachedImages = post.attachedImages,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f / 0.85f)
                    .padding(start = 5.dp, top = 15.dp, end = 5.dp)
            )
        }
        IdeaPostOffice(
            office = post.office,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )
        LikesDislikesCommentsSection(
            likesCount = post.likesCount,
            isLikePressed = post.isLikePressed,
            onLikeClick = onLikeClick,
            dislikesCount = post.dislikesCount,
            isDislikePressed = post.isDislikePressed,
            onDislikeClick = onDislikeClick,
            commentsCount = post.commentsCount,
            onCommentClick = onCommentsClick,
            modifier = Modifier.padding(start = 16.dp, top = 18.dp, bottom = 14.dp)
        )
    }
}

@Composable
private fun MenuSection(
    isAuthorPostCurrentUser: Boolean,
    isIdeaSuggestedToMyOffice: Boolean,
    navigateToAuthorScreen: () -> Unit,
    navigateToEditIdeaScreen: () -> Unit,
    onDeletePostClick: () -> Unit,
    onSuggestIdeaToMyOfficeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        var isMenuVisible by remember { mutableStateOf(false) }
        MenuButton(
            onClick = { isMenuVisible = !isMenuVisible },
            modifier = Modifier.size(26.dp)
        )
        IdeaPostDropdownMenu(
            expanded = isMenuVisible,
            isIdeaSuggestedToMyOffice = isIdeaSuggestedToMyOffice,
            onDismissClick = { isMenuVisible = false },
            isAuthorPostCurrentUser = isAuthorPostCurrentUser,
            onSuggestIdeaToMyOfficeClick = {
                onSuggestIdeaToMyOfficeClick()
                isMenuVisible = false
            },
            onNavigateToAuthorClick = {
                navigateToAuthorScreen()
                isMenuVisible = false
            },
            onEditClick = {
                navigateToEditIdeaScreen()
                isMenuVisible = false
            },
            onDeleteClick = {
                onDeletePostClick()
                isMenuVisible = false
            },
            shape = MaterialTheme.shapes.medium,
            containerColor = MaterialTheme.colorScheme.background,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            menuItemPaddings = PaddingValues(13.dp),
            borderStroke = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            ),
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun IdeaPostAuthor(
    ideaAuthor: IdeaAuthor,
    postDate: LocalDateTime,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        AsyncImage(
            model = ideaAuthor.photo,
            contentDescription = "idea_post_author_photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Text(
                text = "${ideaAuthor.name} ${ideaAuthor.surname}",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp
            )
            Text(
                text = postDate.toRussianString(),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
private fun AppliedSortingFilter(
    sortingCategory: SortingCategory,
    onSortingFilterRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Text(
            text = "${stringResource(featureFiltersResources.string.feature_filters_sort_by)}:",
            style = MaterialTheme.typography.labelMedium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.outline
        )
        SortingCategoryFilter(
            sortingCategory = sortingCategory,
            onRemoveClick = onSortingFilterRemoveClick
        )
    }
}

@Composable
private fun AppliedOfficeFilters(
    officeFilters: List<OfficeFilterUiState>,
    onOfficeFilterRemoveClick: (OfficeFilterUiState) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        modifier = modifier.horizontalScroll(rememberScrollState())
    ) {
        Text(
            text = "${stringResource(featureFiltersResources.string.feature_filters_offices)}:",
            style = MaterialTheme.typography.labelMedium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.outline
        )
        officeFilters.forEach {
            if (it.isSelected) {
                OfficeFilterSearch(
                    office = it.office,
                    size = DpSize(width = 190.dp, height = 50.dp),
                    onRemoveClick = { onOfficeFilterRemoveClick(it) }
                )
                Spacer(modifier = Modifier.width(20.dp))
            }
        }
    }
}

@Composable
private fun SearchTextField(
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    onFiltersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = MaterialTheme.shapes.small
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            .height(IntrinsicSize.Min)
    ) {
        TextField(
            value = searchValue,
            onValueChange = onSearchValueChange,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
            leadingIcon = {
                Icon(
                    painter = painterResource(coreUiResources.drawable.core_ui_outline_search_24),
                    contentDescription = "search_icon",
                    tint = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(start = 10.dp)
                )
            },
            placeholder = {
                Text(
                    text = stringResource(coreUiResources.string.core_ui_search_ideas),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        VerticalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.fillMaxHeight(0.7f)
        )
        Icon(
            painter = painterResource(coreUiResources.drawable.core_ui_baseline_tune_24),
            contentDescription = "filters_icon",
            tint = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .clip(
                    shape.copy(
                        topStart = CornerSize(0.dp),
                        bottomStart = CornerSize(0.dp)
                    )
                )
                .fillMaxHeight()
                .clickable(onClick = onFiltersClick)
                .padding(start = 15.dp, end = 10.dp)
        )
    }
}

@Composable
private fun OfficeFilterSearch(
    office: Office,
    size: DpSize,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = MaterialTheme.shapes.medium
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = shape
            )
    ) {
        val closeIconSize = 22.dp
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = rememberAsyncImagePainter(office.imageUrl),
                contentDescription = "office_image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(
                        shape.copy(
                            topEnd = CornerSize(0.dp),
                            bottomEnd = CornerSize(0.dp)
                        )
                    )
                    .aspectRatio(1f / 1f)
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = office.address,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 12.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.padding(end = closeIconSize)
            )
        }
        Icon(
            painter = painterResource(coreUiResources.drawable.core_ui_baseline_close_24),
            contentDescription = "close_icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 1.dp, end = 3.dp)
                .size(closeIconSize)
                .align(Alignment.TopEnd)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onRemoveClick
                )
        )
    }
}

@Composable
private fun SortingCategoryFilter(
    sortingCategory: SortingCategory,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val closeIconSize = 20.dp
    var textWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box(
        modifier = modifier.height(30.dp)
    ) {
        Text(
            text = sortingCategoryName(sortingCategory),
            style = MaterialTheme.typography.labelMedium,
            fontSize = 14.sp,
            modifier = Modifier
                .padding(end = closeIconSize)
                .align(Alignment.Center)
                .onSizeChanged {
                    textWidth = with(density) { it.width.toDp() }
                }
        )
        Icon(
            painter = painterResource(coreUiResources.drawable.core_ui_baseline_close_24),
            contentDescription = "close_icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(start = textWidth)
                .size(closeIconSize)
                .align(Alignment.TopEnd)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onRemoveClick
                )
        )
    }
}

@Composable
fun MenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(coreUiResources.drawable.core_ui_baseline_more_vert_24),
        contentDescription = "post_info_icon",
        tint = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
    )
}

@Composable
fun AttachedImages(
    attachedImages: List<Any>,
    modifier: Modifier = Modifier
) {
    val attachedImagesPagerState = rememberPagerState(pageCount = { attachedImages.size })
    Box(modifier = modifier) {
        HorizontalPager(
            state = attachedImagesPagerState,
            pageSpacing = 4.dp,
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImageWithLoading(
                model = attachedImages[it],
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
            )
        }
        CurrentImageSection(
            currentImage = attachedImagesPagerState.currentPage + 1,
            imageCount = attachedImagesPagerState.pageCount,
            durationVisibility = 5000L,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 15.dp, end = 15.dp)
        )
    }
}

@Composable
private fun IdeaPostOffice(
    office: Office,
    modifier: Modifier = Modifier
) {
    val officeShape = RoundedCornerShape(30.dp)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(officeShape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = officeShape
            )
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                shape = officeShape
            )
    ) {
        AsyncImageWithLoading(
            model = office.imageUrl,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )
        Text(
            text = office.address,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 10.dp, end = 20.dp)
        )
    }
}

@Composable
fun LikesDislikesCommentsSection(
    likesCount: Int,
    isLikePressed: Boolean,
    onLikeClick: () -> Unit,
    dislikesCount: Int,
    isDislikePressed: Boolean,
    onDislikeClick: () -> Unit,
    commentsCount: Int,
    onCommentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        modifier = modifier
    ) {
        LikesAndDislikesSection(
            isLikePressed = isLikePressed,
            likesCount = likesCount,
            onLikeClick = onLikeClick,
            isDislikePressed = isDislikePressed,
            dislikesCount = dislikesCount,
            onDislikeClick = onDislikeClick,
            spaceBetweenCategories = 15.dp,
            likesIconSize = 18.dp,
            dislikesIconSize = 18.dp,
            textSize = 14.sp,
            buttonsWithBackground = true,
            buttonsWithRippleEffect = true
        )
        ActionItem(
            iconId = coreUiResources.drawable.core_ui_outline_chat_bubble_outline_24,
            count = commentsCount,
            color = MaterialTheme.colorScheme.surfaceVariant,
            onClick = onCommentClick
        )
    }
}

@Composable
private fun ActionItem(
    @DrawableRes iconId: Int,
    count: Int,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .background(color.copy(alpha = 0.2f))
    ) {
        Icon(
            painter = painterResource(iconId),
            contentDescription = "icon_$iconId",
            tint = color,
            modifier = Modifier
                .padding(start = 10.dp, top = 6.dp, end = 7.dp, bottom = 6.dp)
                .size(18.dp)
        )
        Text(
            text = count.toThousandsString(),
            style = MaterialTheme.typography.labelMedium,
            fontSize = 14.sp,
            color = color,
            modifier = Modifier.padding(end = 10.dp)
        )
    }
}

@Composable
fun CurrentImageSection(
    currentImage: Int,
    imageCount: Int,
    durationVisibility: Long,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(currentImage, imageCount) {
        visible = true
        delay(durationVisibility)
        visible = false
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 200)),
        exit = fadeOut(animationSpec = tween(durationMillis = 200)),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .background(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.small
                )
        ) {
            Text(
                text = "$currentImage/$imageCount",
                style = MaterialTheme.typography.labelMedium,
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 8.dp)
            )
        }
    }
}

private fun isScreenLoading(
    currentUserUiState: CurrentUserUiState,
    filtersUiState: FiltersUiState,
    suggestIdeaToMyOfficeUiState: SuggestIdeaToMyOfficeUiState,
    deletePostUiState: DeletePostUiState
) = currentUserUiState.isLoading ||
        filtersUiState.isLoading ||
        suggestIdeaToMyOfficeUiState.isLoading ||
        deletePostUiState.isLoading

private fun isErrorWhileLoading(
    currentUserUiState: CurrentUserUiState,
    filtersUiState: FiltersUiState
): Boolean {
    val isErrorWhileUserLoading = currentUserUiState.isErrorWhileLoading
    val isErrorWhileFiltersLoading = filtersUiState.isErrorWhileLoading
    return isErrorWhileUserLoading || isErrorWhileFiltersLoading
}

@Preview
@Composable
private fun HomeAppBarPreview() {
    OfficeAppTheme {
        HomeAppBar(
            searchUiState = SearchUiState(),
            onSearchValueChange = {},
            onFiltersClick = {},
            filtersUiState = FiltersUiState(),
            onSortingFilterRemoveClick = {},
            onOfficeFilterRemoveClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun OfficeFilterSearchPreview() {
    OfficeAppTheme {
        Surface {
            OfficeFilterSearch(
                office = Office(
                    id = 1,
                    imageUrl = "",
                    address = ""
                ),
                size = DpSize(width = 190.dp, height = 50.dp),
                onRemoveClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun IdeaPostOfficePreview() {
    OfficeAppTheme {
        Surface {
            IdeaPostOffice(
                office = Office(
                    id = 1,
                    imageUrl = "",
                    address = "ул. Нижегородская 2"
                )
            )
        }
    }
}