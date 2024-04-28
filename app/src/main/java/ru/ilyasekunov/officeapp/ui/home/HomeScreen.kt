package ru.ilyasekunov.officeapp.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.runtime.setValue
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
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.data.datasource.local.mock.ideaAuthor
import ru.ilyasekunov.officeapp.data.datasource.local.mock.ideaPost
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.SortingCategories
import ru.ilyasekunov.officeapp.data.model.SortingCategory
import ru.ilyasekunov.officeapp.ui.AnimatedLoadingScreen
import ru.ilyasekunov.officeapp.ui.ErrorScreen
import ru.ilyasekunov.officeapp.ui.LocalCoroutineScope
import ru.ilyasekunov.officeapp.ui.LocalCurrentNavigationBarScreen
import ru.ilyasekunov.officeapp.ui.LocalSnackbarHostState
import ru.ilyasekunov.officeapp.ui.components.AsyncImageWithLoading
import ru.ilyasekunov.officeapp.ui.components.BasicPullToRefreshContainer
import ru.ilyasekunov.officeapp.ui.components.BottomNavigationBar
import ru.ilyasekunov.officeapp.ui.components.LikesAndDislikesSection
import ru.ilyasekunov.officeapp.ui.components.SuggestIdeaButton
import ru.ilyasekunov.officeapp.ui.components.defaultSuggestIdeaFABScrollBehaviour
import ru.ilyasekunov.officeapp.ui.deletePostSnackbar
import ru.ilyasekunov.officeapp.ui.filters.FiltersUiState
import ru.ilyasekunov.officeapp.ui.modifiers.shadow
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme
import ru.ilyasekunov.officeapp.util.isAppending
import ru.ilyasekunov.officeapp.util.isEmpty
import ru.ilyasekunov.officeapp.util.isError
import ru.ilyasekunov.officeapp.util.isRefreshing
import ru.ilyasekunov.officeapp.util.toRussianString
import ru.ilyasekunov.officeapp.util.toThousandsString
import java.time.LocalDateTime

@Composable
fun HomeScreen(
    posts: LazyPagingItems<IdeaPost>,
    currentUserUiState: CurrentUserUiState,
    searchUiState: SearchUiState,
    onSearchValueChange: (String) -> Unit,
    filtersUiState: FiltersUiState,
    onOfficeFilterRemoveClick: (OfficeFilterUiState) -> Unit,
    onSortingFilterRemoveClick: () -> Unit,
    onDeletePostClick: (IdeaPost) -> Unit,
    onPostLikeClick: (IdeaPost) -> Unit,
    onPostDislikeClick: (IdeaPost) -> Unit,
    onRetryInfoLoad: () -> Unit,
    onPullToRefresh: () -> Unit,
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
    val coroutineScope = LocalCoroutineScope.current
    val snackbarHostState = LocalSnackbarHostState.current
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
            currentUserUiState.isUnauthorized -> {
                navigateToAuthGraph()
            }

            isScreenLoading(posts, currentUserUiState, filtersUiState) -> AnimatedLoadingScreen()
            isErrorWhileLoading(posts, currentUserUiState, filtersUiState) -> {
                ErrorScreen(
                    message = stringResource(R.string.error_connecting_to_server),
                    onRetryButtonClick = onRetryInfoLoad
                )
            }

            else -> {
                BasicPullToRefreshContainer(
                    onRefreshTrigger = onPullToRefresh,
                    modifier = Modifier.padding(paddingValues)
                ) {
                    val postDeletedMessage = stringResource(R.string.post_deleted)
                    val undoLabel = stringResource(R.string.undo)
                    IdeaPosts(
                        posts = posts,
                        isIdeaAuthorCurrentUser = { it.id == currentUserUiState.user!!.id },
                        onDeletePostClick = {
                            deletePostSnackbar(
                                snackbarHostState = snackbarHostState,
                                coroutineScope = coroutineScope,
                                message = postDeletedMessage,
                                undoLabel = undoLabel,
                                onSnackbarTimeOut = { onDeletePostClick(it) }
                            )
                        },
                        onPostLikeClick = onPostLikeClick,
                        onPostDislikeClick = onPostDislikeClick,
                        navigateToIdeaDetailsScreen = navigateToIdeaDetailsScreen,
                        navigateToAuthorScreen = navigateToAuthorScreen,
                        navigateToEditIdeaScreen = navigateToEditIdeaScreen,
                        contentPadding = PaddingValues(top = 18.dp, bottom = 18.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(suggestIdeaFABScrollBehaviour.nestedScrollConnection)
                    )
                }
            }
        }
    }
}

@Composable
private fun IdeaPosts(
    posts: LazyPagingItems<IdeaPost>,
    isIdeaAuthorCurrentUser: (IdeaAuthor) -> Boolean,
    onDeletePostClick: (IdeaPost) -> Unit,
    onPostLikeClick: (post: IdeaPost) -> Unit,
    onPostDislikeClick: (post: IdeaPost) -> Unit,
    navigateToIdeaDetailsScreen: (postId: Long, initiallyScrollToComments: Boolean) -> Unit,
    navigateToAuthorScreen: (authorId: Long) -> Unit,
    navigateToEditIdeaScreen: (postId: Long) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        when {
            posts.isEmpty() && !posts.isRefreshing() -> {
                item {
                    NoPostsAvailable(modifier = Modifier.fillMaxSize())
                }
            }
            else -> {
                items(
                    count = posts.itemCount,
                    key = posts.itemKey { it.id }
                ) {
                    val post = posts[it]!!
                    IdeaPost(
                        ideaPost = post,
                        isAuthorPostCurrentUser = isIdeaAuthorCurrentUser(post.ideaAuthor),
                        onPostClick = {
                            navigateToIdeaDetailsScreen(post.id, false)
                        },
                        onLikeClick = { onPostLikeClick(post) },
                        onDislikeClick = { onPostDislikeClick(post) },
                        onCommentClick = {
                            navigateToIdeaDetailsScreen(post.id, true)
                        },
                        navigateToAuthorScreen = navigateToAuthorScreen,
                        navigateToEditIdeaScreen = navigateToEditIdeaScreen,
                        onDeletePostClick = onDeletePostClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (posts.isAppending()) {
                    item {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp,
                            modifier = Modifier
                                .padding(bottom = 20.dp)
                                .fillMaxWidth()
                                .size(20.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
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
private fun NoPostsAvailable(
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.no_posts),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun IdeaPost(
    ideaPost: IdeaPost,
    isAuthorPostCurrentUser: Boolean,
    onPostClick: () -> Unit,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    navigateToAuthorScreen: (authorId: Long) -> Unit,
    navigateToEditIdeaScreen: (postId: Long) -> Unit,
    onDeletePostClick: (IdeaPost) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.large
            )
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick = onPostClick)
    ) {
        val topPadding = 16.dp
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            IdeaPostAuthor(
                ideaAuthor = ideaPost.ideaAuthor,
                postDate = ideaPost.date,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navigateToAuthorScreen(ideaPost.ideaAuthor.id) }
                    .padding(
                        start = 20.dp,
                        top = topPadding,
                        bottom = 10.dp,
                        end = 20.dp
                    )
            )
            Text(
                text = ideaPost.title,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 20.dp, bottom = 25.dp)
            )
            Text(
                text = ideaPost.content,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
                    .heightIn(max = 85.dp)
            )
            if (ideaPost.attachedImages.isNotEmpty()) {
                AttachedImages(
                    attachedImages = ideaPost.attachedImages,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f / 0.85f)
                        .padding(start = 5.dp, top = 15.dp, end = 5.dp)
                )
            }
            IdeaPostOffice(
                office = ideaPost.office,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            LikesDislikesCommentsSection(
                likesCount = ideaPost.likesCount,
                isLikePressed = ideaPost.isLikePressed,
                onLikeClick = onLikeClick,
                dislikesCount = ideaPost.dislikesCount,
                isDislikePressed = ideaPost.isDislikePressed,
                onDislikeClick = onDislikeClick,
                commentsCount = ideaPost.commentsCount,
                onCommentClick = onCommentClick,
                modifier = Modifier
                    .padding(start = 16.dp, top = 18.dp, bottom = 14.dp)
            )
        }
        MenuSection(
            isAuthorPostCurrentUser = isAuthorPostCurrentUser,
            navigateToAuthorScreen = { navigateToAuthorScreen(ideaPost.ideaAuthor.id) },
            navigateToEditIdeaScreen = { navigateToEditIdeaScreen(ideaPost.id) },
            onDeletePostClick = { onDeletePostClick(ideaPost) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = topPadding, end = 14.dp)
        )
    }
}

@Composable
private fun MenuSection(
    isAuthorPostCurrentUser: Boolean,
    navigateToAuthorScreen: () -> Unit,
    navigateToEditIdeaScreen: () -> Unit,
    onDeletePostClick: () -> Unit,
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
            onDismissClick = { isMenuVisible = false },
            isAuthorPostCurrentUser = isAuthorPostCurrentUser,
            onSuggestIdeaToMyOfficeClick = {
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
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        AsyncImage(
            model = ideaAuthor.photo,
            contentDescription = "idea_post_author_photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = "${ideaAuthor.name} ${ideaAuthor.surname}",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(13.dp))
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
        modifier = modifier
    ) {
        Text(
            text = "${stringResource(R.string.sort_by)}:",
            style = MaterialTheme.typography.labelMedium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.width(8.dp))
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
        modifier = modifier.horizontalScroll(rememberScrollState())
    ) {
        Text(
            text = "${stringResource(R.string.offices)}:",
            style = MaterialTheme.typography.labelMedium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.width(18.dp))
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
                    painter = painterResource(R.drawable.outline_search_24),
                    contentDescription = "search_icon",
                    tint = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(start = 10.dp)
                )
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.search_ideas),
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
            painter = painterResource(R.drawable.baseline_tune_24),
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
            painter = painterResource(R.drawable.baseline_close_24),
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
            painter = painterResource(R.drawable.baseline_close_24),
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
        painter = painterResource(R.drawable.baseline_more_vert_24),
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

@OptIn(ExperimentalFoundationApi::class)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun IdeaPostOffice(
    office: Office,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(30.dp)
            )
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
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
            modifier = Modifier
                .padding(start = 10.dp, end = 20.dp)
                .basicMarquee()
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
        Spacer(modifier = Modifier.width(15.dp))
        ActionItem(
            iconId = R.drawable.outline_chat_bubble_outline_24,
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

@Composable
fun sortingCategoryName(sortingCategory: SortingCategory) =
    when (sortingCategory.id) {
        SortingCategories.COMMENTS.id -> stringResource(R.string.by_comments)
        SortingCategories.LIKES.id -> stringResource(R.string.by_likes)
        SortingCategories.DISLIKES.id -> stringResource(R.string.by_dislikes)
        else -> throw IllegalStateException("Unknown sorting category - $sortingCategory")
    }

private fun isScreenLoading(
    posts: LazyPagingItems<IdeaPost>,
    currentUserUiState: CurrentUserUiState,
    filtersUiState: FiltersUiState
) = posts.isRefreshing() || currentUserUiState.isLoading || filtersUiState.isLoading


private fun isErrorWhileLoading(
    posts: LazyPagingItems<IdeaPost>,
    currentUserUiState: CurrentUserUiState,
    filtersUiState: FiltersUiState
): Boolean {
    val isErrorWhileUserLoading = currentUserUiState.isErrorWhileLoading
    val isErrorWhileFiltersLoading = filtersUiState.isErrorWhileLoading
    return posts.isError() || isErrorWhileUserLoading || isErrorWhileFiltersLoading
}

@Preview
@Composable
private fun IdeaPostPreview() {
    OfficeAppTheme {
        Surface {
            IdeaPost(
                ideaPost = ideaPost,
                isAuthorPostCurrentUser = true,
                onPostClick = {},
                onLikeClick = {},
                onDislikeClick = {},
                onCommentClick = {},
                navigateToAuthorScreen = {},
                navigateToEditIdeaScreen = {},
                onDeletePostClick = {}
            )
        }
    }
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
private fun IdeaPostAuthorPreview() {
    OfficeAppTheme {
        Surface {
            IdeaPostAuthor(
                ideaAuthor = ideaAuthor,
                postDate = LocalDateTime.now()
            )
        }
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
private fun SortingCategoryFilterPreview() {
    OfficeAppTheme {
        Surface {
            SortingCategoryFilter(sortingCategory = SortingCategory(id = 0, "Комментариям"),
                onRemoveClick = {})
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
                    id = 1, imageUrl = "", address = ""
                )
            )
        }
    }
}