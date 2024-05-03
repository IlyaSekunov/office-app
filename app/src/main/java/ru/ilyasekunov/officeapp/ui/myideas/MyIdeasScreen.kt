package ru.ilyasekunov.officeapp.ui.myideas

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.ui.ErrorScreen
import ru.ilyasekunov.officeapp.ui.LoadingScreen
import ru.ilyasekunov.officeapp.ui.LocalCoroutineScope
import ru.ilyasekunov.officeapp.ui.LocalCurrentNavigationBarScreen
import ru.ilyasekunov.officeapp.ui.LocalSnackbarHostState
import ru.ilyasekunov.officeapp.ui.components.AsyncImageWithLoading
import ru.ilyasekunov.officeapp.ui.components.BothDirectedPullToRefreshContainer
import ru.ilyasekunov.officeapp.ui.components.BottomNavigationBar
import ru.ilyasekunov.officeapp.ui.components.LazyPagingItemsVerticalGrid
import ru.ilyasekunov.officeapp.ui.favouriteideas.rememberRandomFavouriteIdeaColor
import ru.ilyasekunov.officeapp.ui.modifiers.shadow
import ru.ilyasekunov.officeapp.ui.snackbarWithAction

@Composable
fun MyIdeasScreen(
    ideas: LazyPagingItems<IdeaPost>,
    onPullToRefresh: CoroutineScope.() -> Job,
    onDeleteIdeaClick: (IdeaPost) -> Unit,
    navigateToIdeaDetailsScreen: (Long) -> Unit,
    navigateToSuggestIdeaScreen: () -> Unit,
    navigateToEditIdeaScreen: (Long) -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateBack: () -> Unit
) {
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
        snackbarHost = { SnackbarHost(hostState = LocalSnackbarHostState.current) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
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
    var selectedIdea by remember { mutableStateOf<IdeaPost?>(null) }
    var isBottomSheetVisible by remember { mutableStateOf(false) }
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
                    onLongClick = {
                        selectedIdea = idea
                        isBottomSheetVisible = true
                    },
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
                    message = stringResource(R.string.error_while_ideas_loading),
                    onRetryButtonClick = ideas::retry
                )
            },
            errorWhileAppendComposable = {
                ErrorWhileAppending(
                    message = stringResource(R.string.error_while_ideas_loading),
                    modifier = Modifier.size(ideaSize)
                )
            },
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxSize()
        )
    }
    val postDeletedMessage = stringResource(R.string.post_deleted)
    val undoLabel = stringResource(R.string.undo)
    if (isBottomSheetVisible) {
        MyIdeaActionsBottomSheet(
            onDismiss = {
                isBottomSheetVisible = false
                selectedIdea = null
            },
            onEditClick = {
                selectedIdea?.let { navigateToEditIdeaScreen(it.id) }
                isBottomSheetVisible = false
                selectedIdea = null
            },
            onDeleteClick = {
                coroutineScope.launch {
                    snackbarHostState.snackbarWithAction(
                        message = postDeletedMessage,
                        actionLabel = undoLabel,
                        onTimeout = {
                            selectedIdea?.let { onDeleteIdeaClick(it) }
                            selectedIdea = null
                            coroutineScope.launch { onPullToRefresh() }
                        },
                        duration = SnackbarDuration.Short
                    )
                }
                isBottomSheetVisible = false
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
        modifier = modifier
    ) {
        MyIdeaAction(
            icon = R.drawable.outline_create_24,
            text = stringResource(R.string.edit),
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth()
        )
        MyIdeaAction(
            icon = R.drawable.outline_delete_24,
            text = stringResource(R.string.delete),
            onClick = onDeleteClick,
            modifier = Modifier.fillMaxWidth()
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
            .padding(20.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "delete_icon",
            tint = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.surfaceVariant,
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
                text = stringResource(R.string.my_ideas),
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
            text = stringResource(R.string.my_ideas_list_is_empty),
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
            text = stringResource(R.string.suggest_an_idea),
            style = MaterialTheme.typography.labelLarge,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}