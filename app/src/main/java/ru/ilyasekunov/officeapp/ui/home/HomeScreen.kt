package ru.ilyasekunov.officeapp.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.data.datasource.local.mock.ideaAuthor
import ru.ilyasekunov.officeapp.data.datasource.local.mock.ideaPost
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.SortingCategories
import ru.ilyasekunov.officeapp.data.model.SortingCategory
import ru.ilyasekunov.officeapp.navigation.BottomNavigationScreen
import ru.ilyasekunov.officeapp.ui.ErrorScreen
import ru.ilyasekunov.officeapp.ui.LoadingScreen
import ru.ilyasekunov.officeapp.ui.animations.dislikePressedAnimation
import ru.ilyasekunov.officeapp.ui.animations.likePressedAnimation
import ru.ilyasekunov.officeapp.ui.components.BasicPullToRefreshContainer
import ru.ilyasekunov.officeapp.ui.components.BottomNavigationBar
import ru.ilyasekunov.officeapp.ui.modifiers.shadow
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme
import ru.ilyasekunov.officeapp.ui.theme.dislikePressedColor
import ru.ilyasekunov.officeapp.ui.theme.likePressedColor
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
    onPostLikeClick: (post: IdeaPost, isPressed: Boolean) -> Unit,
    onPostDislikeClick: (post: IdeaPost, isPressed: Boolean) -> Unit,
    onCommentClick: (IdeaPost) -> Unit,
    onRetryInfoLoad: () -> Unit,
    onPullToRefresh: () -> Unit,
    navigateToSuggestIdeaScreen: () -> Unit,
    navigateToFiltersScreen: () -> Unit,
    navigateToIdeaDetailsScreen: (postId: Long) -> Unit,
    navigateToAuthorScreen: (authorId: Long) -> Unit,
    navigateToEditIdeaScreen: (postId: Long) -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateToAuthGraph: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
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
                selectedScreen = BottomNavigationScreen.Home,
                navigateToHomeScreen = {},
                navigateToFavouriteScreen = navigateToFavouriteScreen,
                navigateToMyOfficeScreen = navigateToMyOfficeScreen,
                navigateToProfileScreen = navigateToProfileScreen,
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            SuggestIdeaButton(onClick = navigateToSuggestIdeaScreen)
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        val postsRefreshing = posts.loadState.refresh == LoadState.Loading
        val isErrorWhilePostsLoading = posts.loadState.hasError
        when {
            currentUserUiState.isUnauthorized -> {
                navigateToAuthGraph()
            }

            postsRefreshing || currentUserUiState.isLoading || filtersUiState.isLoading -> {
                LoadingScreen()
            }

            isErrorWhilePostsLoading || filtersUiState.isErrorWhileLoading || currentUserUiState.isErrorWhileLoading -> {
                ErrorScreen(
                    message = stringResource(R.string.error_connecting_to_server),
                    onRetryButtonClick = onRetryInfoLoad
                )
            }

            posts.itemCount == 0 -> {
                BasicPullToRefreshContainer(
                    onRefreshTrigger = onPullToRefresh,
                    modifier = Modifier.padding(paddingValues)
                ) {
                    NoPostsAvailable(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    )
                }
            }

            else -> {
                val postDeletedMessage = stringResource(R.string.post_deleted)
                val undoLabel = stringResource(R.string.undo)
                BasicPullToRefreshContainer(
                    onRefreshTrigger = onPullToRefresh,
                    modifier = Modifier.padding(paddingValues)
                ) {
                    IdeaPosts(
                        posts = posts,
                        isIdeaAuthorCurrentUser = { it.id == currentUserUiState.user!!.id },
                        onDeletePostClick = {
                            deletePostSnackbar(
                                coroutineScope = coroutineScope,
                                snackbarHostState = snackbarHostState,
                                message = postDeletedMessage,
                                undoLabel = undoLabel,
                                onSnackbarTimeOut = { onDeletePostClick(it) }
                            )
                        },
                        onPostLikeClick = onPostLikeClick,
                        onPostDislikeClick = onPostDislikeClick,
                        onCommentClick = onCommentClick,
                        navigateToIdeaDetailsScreen = navigateToIdeaDetailsScreen,
                        navigateToAuthorScreen = navigateToAuthorScreen,
                        navigateToEditIdeaScreen = navigateToEditIdeaScreen,
                        contentPadding = PaddingValues(top = 18.dp, bottom = 18.dp),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun IdeaPosts(
    posts: LazyPagingItems<IdeaPost>,
    isIdeaAuthorCurrentUser: (IdeaAuthor) -> Boolean,
    onDeletePostClick: (IdeaPost) -> Unit,
    onPostLikeClick: (post: IdeaPost, isPressed: Boolean) -> Unit,
    onPostDislikeClick: (post: IdeaPost, isPressed: Boolean) -> Unit,
    onCommentClick: (IdeaPost) -> Unit,
    navigateToIdeaDetailsScreen: (postId: Long) -> Unit,
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
        items(
            count = posts.itemCount,
            key = posts.itemKey { it.id }
        ) {
            val post = posts[it]!!
            IdeaPost(
                ideaPost = post,
                isAuthorPostCurrentUser = isIdeaAuthorCurrentUser(post.ideaAuthor),
                onPostClick = {
                    navigateToIdeaDetailsScreen(post.id)
                },
                onLikeClick = {
                    onPostLikeClick(post, !post.isLikePressed)
                },
                onDislikeClick = {
                    onPostDislikeClick(post, !post.isDislikePressed)
                },
                onCommentClick = {
                    onCommentClick(post)
                },
                navigateToAuthorScreen = navigateToAuthorScreen,
                navigateToEditIdeaScreen = navigateToEditIdeaScreen,
                onDeletePostClick = onDeletePostClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (posts.loadState.append == LoadState.Loading) {
            item {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(20.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
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
    modifier: Modifier = Modifier
) {
    val sortingFiltersUiState = filtersUiState.sortingFiltersUiState
    val officeFilterUiState = filtersUiState.officeFiltersUiState
    val showSortingFilter = sortingFiltersUiState.selected != null
    val showOfficeFilters = filtersUiState.officeFiltersUiState.any { it.isSelected }
    Column(
        modifier = modifier
            .shadow(blurRadius = 6.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 25.dp, start = 13.dp, end = 13.dp, bottom = 10.dp)
    ) {
        SearchTextField(
            searchValue = searchUiState.value,
            onSearchValueChange = onSearchValueChange,
            onFiltersClick = onFiltersClick
        )
        if (showOfficeFilters || showSortingFilter) {
            Spacer(modifier = Modifier.height(20.dp))
            if (showOfficeFilters) {
                AppliedOfficeFilters(
                    officeFilters = officeFilterUiState,
                    onOfficeFilterRemoveClick = onOfficeFilterRemoveClick
                )
            }
            if (showSortingFilter) {
                if (showOfficeFilters) {
                    Spacer(modifier = Modifier.height(30.dp))
                }
                AppliedSortingFilter(
                    sortingCategory = sortingFiltersUiState.selected!!,
                    onSortingFilterRemoveClick = onSortingFilterRemoveClick
                )
            }
        }
    }
}

@Composable
fun NoPostsAvailable(
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
            .clickable { onPostClick() }
    ) {
        val topPadding = 16.dp
        Column(
            modifier = Modifier.padding(top = topPadding)
        ) {
            IdeaPostAuthor(
                ideaAuthor = ideaPost.ideaAuthor,
                postDate = ideaPost.date,
                modifier = Modifier.padding(start = 20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = ideaPost.title,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 20.dp)
            )
            Spacer(modifier = Modifier.height(25.dp))
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
            Spacer(modifier = Modifier.height(16.dp))
            IdeaPostOffice(
                office = ideaPost.office, modifier = Modifier.padding(start = 16.dp)
            )
            Spacer(modifier = Modifier.height(18.dp))
            LikesDislikesCommentsSection(
                likesCount = ideaPost.likesCount,
                isLikePressed = ideaPost.isLikePressed,
                onLikeClick = onLikeClick,
                dislikesCount = ideaPost.dislikesCount,
                isDislikePressed = ideaPost.isDislikePressed,
                onDislikeClick = onDislikeClick,
                commentsCount = ideaPost.commentsCount,
                onCommentClick = onCommentClick,
                modifier = Modifier.padding(start = 16.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
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
fun MenuSection(
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
        IdeaPostMenu(
            expanded = isMenuVisible,
            onDismissClick = { isMenuVisible = false },
            isAuthorPostCurrentUser = isAuthorPostCurrentUser,
            onSuggestIdeaToMyOfficeClick = { /*TODO*/ },
            onNavigateToAuthorClick = navigateToAuthorScreen,
            onEditClick = navigateToEditIdeaScreen,
            onDeleteClick = onDeletePostClick,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}

@Composable
fun IdeaPostAuthor(
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
fun AppliedSortingFilter(
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
fun AppliedOfficeFilters(
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
fun SearchTextField(
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    onFiltersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = MaterialTheme.shapes.small
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .statusBarsPadding()
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
        Icon(painter = painterResource(R.drawable.baseline_tune_24),
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
fun OfficeFilterSearch(
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
            Image(painter = rememberAsyncImagePainter(office.imageUrl),
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
fun SortingCategoryFilter(
    sortingCategory: SortingCategory,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val closeIconSize = 20.dp
    var textWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current.density
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
                .onGloballyPositioned {
                    textWidth = (it.size.width / density).dp
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
fun IdeaPostMenu(
    expanded: Boolean,
    onDismissClick: () -> Unit,
    isAuthorPostCurrentUser: Boolean,
    onSuggestIdeaToMyOfficeClick: () -> Unit,
    onNavigateToAuthorClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val suggestIdeaToMyOffice = stringResource(R.string.suggest_idea_to_my_office)
    val navigateToAuthor = stringResource(R.string.navigate_to_author)
    val editPost = stringResource(R.string.edit)
    val deletePost = stringResource(R.string.delete)
    val optionsList = if (isAuthorPostCurrentUser)
        listOf(
            suggestIdeaToMyOffice,
            editPost,
            deletePost
        )
    else
        listOf(
            suggestIdeaToMyOffice,
            navigateToAuthor
        )
    Box(modifier = modifier) {
        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(10.dp))
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismissClick,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                optionsList.forEach {
                    val onClick = when (it) {
                        suggestIdeaToMyOffice -> onSuggestIdeaToMyOfficeClick
                        navigateToAuthor -> onNavigateToAuthorClick
                        editPost -> onEditClick
                        deletePost -> onDeleteClick
                        else -> throw RuntimeException("Unknown option to dropdown menu - '$it'")
                    }
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp
                            )
                        },
                        contentPadding = PaddingValues(13.dp),
                        onClick = onClick
                    )
                }
            }
        }
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
            pageSpacing = 10.dp,
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = attachedImages[it],
                contentDescription = "attached_image_${it}",
                contentScale = ContentScale.Crop,
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
fun IdeaPostOffice(
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
        Image(
            painter = rememberAsyncImagePainter(office.imageUrl),
            contentDescription = "office_photo",
            contentScale = ContentScale.Crop,
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
    modifier: Modifier = Modifier,
    isLikePressed: Boolean = false,
    onLikeClick: () -> Unit,
    dislikesCount: Int,
    isDislikePressed: Boolean = false,
    onDislikeClick: () -> Unit,
    commentsCount: Int,
    onCommentClick: () -> Unit,
) {
    Row(
        modifier = modifier
    ) {
        LikeButton(
            onClick = onLikeClick,
            iconSize = 18.dp,
            isPressed = isLikePressed,
            count = likesCount
        )
        Spacer(modifier = Modifier.width(15.dp))
        DislikeButton(
            onClick = onDislikeClick,
            iconSize = 18.dp,
            isPressed = isDislikePressed,
            count = dislikesCount
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
fun LikeButton(
    onClick: () -> Unit,
    iconSize: Dp,
    isPressed: Boolean,
    count: Int,
    modifier: Modifier = Modifier
) {
    val animatedIconRotation = remember { Animatable(0f) }
    val animatedIconScale = remember { Animatable(1f) }
    var isAnimationRunning by remember { mutableStateOf(false) }
    if (isAnimationRunning) {
        LaunchedEffect(Unit) {
            likePressedAnimation(
                animatableScale = animatedIconScale,
                animatableRotation = animatedIconRotation
            ).join()
            isAnimationRunning = false
        }
    }

    val color = if (isPressed) likePressedColor else MaterialTheme.colorScheme.surfaceVariant
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
                if (!isPressed) {
                    isAnimationRunning = true
                }
            }
            .background(color.copy(alpha = 0.2f))
    ) {
        Icon(
            painter = painterResource(R.drawable.outline_thumb_up_24),
            contentDescription = "like_icon",
            tint = color,
            modifier = Modifier
                .padding(start = 10.dp, top = 6.dp, end = 7.dp, bottom = 6.dp)
                .size(iconSize)
                .graphicsLayer {
                    scaleX = animatedIconScale.value
                    scaleY = animatedIconScale.value
                    transformOrigin = TransformOrigin(0f, 1f)
                    rotationZ = animatedIconRotation.value
                }
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
fun DislikeButton(
    onClick: () -> Unit,
    iconSize: Dp,
    isPressed: Boolean,
    count: Int,
    modifier: Modifier = Modifier
) {
    val animatedIconRotation = remember { Animatable(0f) }
    val animatedIconScale = remember { Animatable(1f) }
    var isAnimationRunning by remember { mutableStateOf(false) }
    if (isAnimationRunning) {
        LaunchedEffect(Unit) {
            dislikePressedAnimation(
                animatableScale = animatedIconScale, animatableRotation = animatedIconRotation
            ).join()
            isAnimationRunning = false
        }
    }

    val color = if (isPressed) dislikePressedColor else MaterialTheme.colorScheme.surfaceVariant
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
                if (!isPressed) {
                    isAnimationRunning = true
                }
            }
            .background(color.copy(alpha = 0.2f))
    ) {
        Icon(
            painter = painterResource(R.drawable.outline_thumb_down_24),
            contentDescription = "like_icon",
            tint = color,
            modifier = Modifier
                .padding(start = 10.dp, top = 6.dp, end = 7.dp, bottom = 6.dp)
                .size(iconSize)
                .graphicsLayer {
                    scaleX = animatedIconScale.value
                    scaleY = animatedIconScale.value
                    transformOrigin = TransformOrigin(1f, 0f)
                    rotationZ = animatedIconRotation.value
                }
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
fun ActionItem(
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
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
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
fun SuggestIdeaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(50.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Icon(
            painter = painterResource(R.drawable.outline_create_24),
            contentDescription = "create_button",
            modifier = Modifier.size(30.dp)
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

private fun deletePostSnackbar(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    message: String,
    undoLabel: String,
    onSnackbarTimeOut: () -> Unit
) = coroutineScope.launch {
    snackbarHostState.showSnackbar(
        message = message,
        actionLabel = undoLabel,
        duration = SnackbarDuration.Short
    ).also {
        if (it != SnackbarResult.ActionPerformed) {
            onSnackbarTimeOut()
        }
    }
}

@Preview
@Composable
fun IdeaPostPreview() {
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
fun HomeAppBarPreview() {
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
fun IdeaPostAuthorPreview() {
    OfficeAppTheme {
        Surface {
            IdeaPostAuthor(
                ideaAuthor = ideaAuthor, postDate = LocalDateTime.now()
            )
        }
    }
}

@Preview
@Composable
fun OfficeFilterSearchPreview() {
    OfficeAppTheme {
        Surface {
            OfficeFilterSearch(office = Office(
                id = 1, imageUrl = "", address = ""
            ), size = DpSize(width = 190.dp, height = 50.dp), onRemoveClick = {})
        }
    }
}

@Preview
@Composable
fun SortingCategoryFilterPreview() {
    OfficeAppTheme {
        Surface {
            SortingCategoryFilter(sortingCategory = SortingCategory(id = 0, "Комментариям"),
                onRemoveClick = {})
        }
    }
}

@Preview
@Composable
fun IdeaPostOfficePreview() {
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

@Preview
@Composable
fun SuggestIdeaButtonPreview() {
    OfficeAppTheme {
        Surface {
            SuggestIdeaButton(onClick = {})
        }
    }
}