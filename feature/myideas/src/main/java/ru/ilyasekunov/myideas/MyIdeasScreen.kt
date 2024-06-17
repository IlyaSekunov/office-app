package ru.ilyasekunov.myideas

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.ilyasekunov.favouriteideas.rememberRandomFavouriteIdeaColor
import ru.ilyasekunov.home.DeletePostUiState
import ru.ilyasekunov.home.ObserveDeletePostUiState
import ru.ilyasekunov.model.IdeaPost
import ru.ilyasekunov.ui.AnimatedLoadingScreen
import ru.ilyasekunov.ui.ErrorScreen
import ru.ilyasekunov.ui.LoadingScreen
import ru.ilyasekunov.ui.LocalCoroutineScope
import ru.ilyasekunov.ui.LocalCurrentNavigationBarScreen
import ru.ilyasekunov.ui.LocalSnackbarHostState
import ru.ilyasekunov.ui.components.AsyncImageWithLoading
import ru.ilyasekunov.ui.components.BothDirectedPullToRefreshContainer
import ru.ilyasekunov.ui.components.BottomNavigationBar
import ru.ilyasekunov.ui.components.LazyPagingItemsVerticalGrid
import ru.ilyasekunov.ui.modifiers.shadow
import ru.ilyasekunov.ui.snackbarWithAction
import ru.ilyasekunov.officeapp.core.ui.R as coreUiResources
import ru.ilyasekunov.officeapp.feature.suggestidea.R as featureSuggestIdea

@Composable
fun MyIdeasScreen(
    ideas: LazyPagingItems<IdeaPost>,
    deletePostUiState: DeletePostUiState,
    onPullToRefresh: CoroutineScope.() -> Job,
    onDeleteIdeaClick: (IdeaPost) -> Unit,
    onDeleteResultShown: () -> Unit,
    navigateToIdeaDetailsScreen: (Long) -> Unit,
    navigateToSuggestIdeaScreen: () -> Unit,
    navigateToEditIdeaScreen: (Long) -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val coroutineScope = LocalCoroutineScope.current

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = LocalCurrentNavigationBarScreen.current,
                navigateToHomeScreen = navigateToHomeScreen,
                navigateToProfileScreen = navigateToProfileScreen,
                navigateToMyOfficeScreen = navigateToMyOfficeScreen,
                navigateToFavouriteScreen = navigateToFavouriteScreen
            )
        },
        topBar = {
            MyIdeasTopAppBar(
                navigateBack = navigateBack,
                modifier = Modifier.fillMaxWidth()
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        when {
            deletePostUiState.isLoading -> AnimatedLoadingScreen()
            else -> {
                MyIdeasScreenContent(
                    ideas = ideas,
                    onPullToRefresh = onPullToRefresh,
                    onDeleteIdeaClick = onDeleteIdeaClick,
                    navigateToIdeaDetailsScreen = navigateToIdeaDetailsScreen,
                    navigateToSuggestIdeaScreen = navigateToSuggestIdeaScreen,
                    navigateToEditIdeaScreen = navigateToEditIdeaScreen,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
                ObserveDeletePostUiState(
                    deletePostUiState = deletePostUiState,
                    snackbarHostState = snackbarHostState,
                    coroutineScope = coroutineScope,
                    onResultShown = onDeleteResultShown,
                    onDeleteSuccess = ideas::refresh
                )
            }
        }
    }
}

@Composable
private fun MyIdeasScreenContent(
    ideas: LazyPagingItems<IdeaPost>,
    onPullToRefresh: CoroutineScope.() -> Job,
    onDeleteIdeaClick: (IdeaPost) -> Unit,
    navigateToIdeaDetailsScreen: (Long) -> Unit,
    navigateToSuggestIdeaScreen: () -> Unit,
    navigateToEditIdeaScreen: (Long) -> Unit,
    modifier: Modifier = Modifier,
    ideaSize: Dp = 100.dp,
    snackbarHostState: SnackbarHostState = LocalSnackbarHostState.current,
    coroutineScope: CoroutineScope = LocalCoroutineScope.current
) {
    var contextSelectedIdea by remember { mutableStateOf<IdeaPost?>(null) }
    BothDirectedPullToRefreshContainer(
        onRefreshTrigger = onPullToRefresh,
        modifier = modifier
    ) { isRefreshing ->
        LazyPagingItemsVerticalGrid(
            items = ideas,
            isPullToRefreshActive = isRefreshing,
            columns = GridCells.Adaptive(minSize = ideaSize),
            itemKey = { it.id },
            itemsEmptyComposable = {
                NoIdeas(onSuggestIdeaButtonClick = navigateToSuggestIdeaScreen)
            },
            itemComposable = { idea ->
                MyIdea(
                    ideaPost = idea,
                    onClick = { navigateToIdeaDetailsScreen(idea.id) },
                    onLongClick = { contextSelectedIdea = idea },
                    modifier = Modifier.size(ideaSize)
                )
            },
            itemsAppendComposable = {
                LoadingScreen(
                    modifier = Modifier
                        .size(ideaSize)
                        .wrapContentSize(Alignment.Center)
                )
            },
            errorWhileRefreshComposable = {
                ErrorScreen(
                    message = stringResource(coreUiResources.string.core_ui_error_while_ideas_loading),
                    onRetryButtonClick = ideas::retry
                )
            },
            errorWhileAppendComposable = {
                ErrorWhileAppending(
                    message = stringResource(coreUiResources.string.core_ui_error_while_ideas_loading),
                    modifier = Modifier.size(ideaSize)
                )
            },
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxSize()
        )
    }

    val postDeletedMessage = stringResource(coreUiResources.string.core_ui_post_will_be_deleted_in_5_seconds)
    val undoLabel = stringResource(coreUiResources.string.core_ui_undo)

    if (contextSelectedIdea != null) {
        MyIdeaActionsBottomSheet(
            onDismiss = { contextSelectedIdea = null },
            onEditClick = {
                contextSelectedIdea?.let { navigateToEditIdeaScreen(it.id) }
                contextSelectedIdea = null
            },
            onDeleteClick = {
                val idea = contextSelectedIdea!!
                coroutineScope.launch {
                    snackbarHostState.snackbarWithAction(
                        message = postDeletedMessage,
                        actionLabel = undoLabel,
                        onTimeout = { onDeleteIdeaClick(idea) },
                        duration = SnackbarDuration.Short
                    )
                }
                contextSelectedIdea = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyIdeaActionsBottomSheet(
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        shape = MaterialTheme.shapes.large.copy(
            bottomEnd = CornerSize(0.dp),
            bottomStart = CornerSize(0.dp)
        ),
        tonalElevation = 0.dp,
        windowInsets = WindowInsets(0.dp),
        modifier = modifier
    ) {
        MyIdeaAction(
            icon = coreUiResources.drawable.core_ui_outline_create_24,
            text = stringResource(coreUiResources.string.core_ui_edit),
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth()
        )
        MyIdeaAction(
            icon = coreUiResources.drawable.core_ui_outline_delete_24,
            text = stringResource(coreUiResources.string.core_ui_delete),
            onClick = onDeleteClick,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        )
    }
}

@Composable
private fun MyIdeaAction(
    @DrawableRes icon: Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "delete_icon",
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 16.sp
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MyIdea(
    ideaPost: IdeaPost,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (ideaPost.attachedImages.isNotEmpty()) {
        AsyncImageWithLoading(
            model = ideaPost.attachedImages.first(),
            modifier = modifier
                .fillMaxSize()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
        )
    } else {
        val color = rememberRandomFavouriteIdeaColor()
        Text(
            text = ideaPost.title,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = modifier
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
                .background(color)
                .wrapContentSize(Alignment.Center)
                .padding(4.dp)
        )
    }
}

@Composable
private fun ErrorWhileAppending(
    message: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = message,
        fontSize = 12.sp,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.surfaceVariant,
        textAlign = TextAlign.Center,
        modifier = modifier.wrapContentSize(Alignment.Center)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyIdeasTopAppBar(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(coreUiResources.string.core_ui_my_ideas),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 20.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "back_arrow",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background
        ),
        modifier = modifier.shadow(blurRadius = 4.dp)
    )
}

@Composable
private fun NoIdeas(
    onSuggestIdeaButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = stringResource(coreUiResources.string.core_ui_my_ideas_list_is_empty),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
        SuggestIdeaButton(onClick = onSuggestIdeaButtonClick)
    }
}

@Composable
private fun SuggestIdeaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "animated_scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .selectable(
                selected = isPressed,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .scale(animatedScale)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primary)
            .padding(vertical = 6.dp, horizontal = 10.dp)
    ) {
        Text(
            text = stringResource(featureSuggestIdea.string.feature_suggestidea_suggest_idea),
            style = MaterialTheme.typography.labelLarge,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}