package ru.ilyasekunov.officeapp.ui.office

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.ui.AnimatedLoadingScreen
import ru.ilyasekunov.officeapp.ui.ErrorScreen
import ru.ilyasekunov.officeapp.ui.LocalCoroutineScope
import ru.ilyasekunov.officeapp.ui.LocalCurrentNavigationBarScreen
import ru.ilyasekunov.officeapp.ui.LocalSnackbarHostState
import ru.ilyasekunov.officeapp.ui.RetryButton
import ru.ilyasekunov.officeapp.ui.components.AsyncImageWithLoading
import ru.ilyasekunov.officeapp.ui.components.BothDirectedPullToRefreshContainer
import ru.ilyasekunov.officeapp.ui.components.BottomNavigationBar
import ru.ilyasekunov.officeapp.ui.components.LazyPagingItemsColumn
import ru.ilyasekunov.officeapp.ui.components.LazyPagingItemsHorizontalGrid
import ru.ilyasekunov.officeapp.ui.deletePostSnackbar
import ru.ilyasekunov.officeapp.ui.home.CurrentUserUiState
import ru.ilyasekunov.officeapp.ui.home.IdeaPost
import ru.ilyasekunov.officeapp.ui.modifiers.conditional
import ru.ilyasekunov.officeapp.util.isEmpty
import kotlin.math.min

private enum class IdeasGroup {
    SUGGESTED, IN_PROGRESS, IMPLEMENTED
}

private enum class InfoGroup {
    IDEAS, EMPLOYEES
}

@Composable
fun MyOfficeScreen(
    currentUserUiState: CurrentUserUiState,
    suggestedIdeas: LazyPagingItems<IdeaPost>,
    ideasInProgress: LazyPagingItems<IdeaPost>,
    implementedIdeas: LazyPagingItems<IdeaPost>,
    officeEmployees: LazyPagingItems<IdeaAuthor>,
    onRetryDataLoad: () -> Unit,
    onPullToRefresh: CoroutineScope.() -> Job,
    onPostLikeClick: (IdeaPost) -> Unit,
    onPostDislikeClick: (IdeaPost) -> Unit,
    onPostCommentsClick: (IdeaPost) -> Unit,
    onDeletePostClick: (IdeaPost) -> Unit,
    navigateToIdeaDetailsScreen: (Long) -> Unit,
    navigateToEditIdeaScreen: (Long) -> Unit,
    navigateToAuthorScreen: (authorId: Long) -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateToAuthGraph: () -> Unit
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val coroutineScope = LocalCoroutineScope.current
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = LocalCurrentNavigationBarScreen.current,
                navigateToHomeScreen = navigateToHomeScreen,
                navigateToFavouriteScreen = navigateToFavouriteScreen,
                navigateToProfileScreen = navigateToProfileScreen,
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .statusBarsPadding()
    ) { paddingValues ->
        when {
            currentUserUiState.isUnauthorized -> {
                navigateToAuthGraph()
            }

            isScreenLoading(currentUserUiState) -> AnimatedLoadingScreen()
            isErrorWhileLoading(currentUserUiState) -> {
                ErrorScreen(
                    message = stringResource(R.string.error_connecting_to_server),
                    onRetryButtonClick = onRetryDataLoad
                )
            }

            else -> {
                val postDeletedMessage = stringResource(R.string.post_deleted)
                val undoLabel = stringResource(R.string.undo)
                MyOfficeScreenContent(
                    currentUserUiState = currentUserUiState,
                    suggestedIdeas = suggestedIdeas,
                    ideasInProgress = ideasInProgress,
                    implementedIdeas = implementedIdeas,
                    officeEmployees = officeEmployees,
                    onPullToRefresh = onPullToRefresh,
                    onPostLikeClick = onPostLikeClick,
                    onPostDislikeClick = onPostDislikeClick,
                    onPostCommentsClick = onPostCommentsClick,
                    onDeletePostClick = {
                        deletePostSnackbar(
                            snackbarHostState = snackbarHostState,
                            coroutineScope = coroutineScope,
                            message = postDeletedMessage,
                            undoLabel = undoLabel,
                            onSnackbarTimeOut = { onDeletePostClick(it) }
                        )
                    },
                    navigateToIdeaDetailsScreen = navigateToIdeaDetailsScreen,
                    navigateToEditIdeaScreen = navigateToEditIdeaScreen,
                    navigateToAuthorScreen = navigateToAuthorScreen,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun MyOfficeScreenContent(
    currentUserUiState: CurrentUserUiState,
    suggestedIdeas: LazyPagingItems<IdeaPost>,
    ideasInProgress: LazyPagingItems<IdeaPost>,
    implementedIdeas: LazyPagingItems<IdeaPost>,
    officeEmployees: LazyPagingItems<IdeaAuthor>,
    onPullToRefresh: CoroutineScope.() -> Job,
    onPostLikeClick: (IdeaPost) -> Unit,
    onPostDislikeClick: (IdeaPost) -> Unit,
    onPostCommentsClick: (IdeaPost) -> Unit,
    onDeletePostClick: (IdeaPost) -> Unit,
    navigateToIdeaDetailsScreen: (Long) -> Unit,
    navigateToEditIdeaScreen: (Long) -> Unit,
    navigateToAuthorScreen: (authorId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    BothDirectedPullToRefreshContainer(
        onRefreshTrigger = onPullToRefresh,
        modifier = modifier
    ) { isRefreshing ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Title(modifier = Modifier.padding(bottom = 40.dp, top = 16.dp))
            OfficeInfo(
                office = currentUserUiState.user!!.office,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f / 1f)
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 24.dp)
            )
            IdeasAndEmployeesInfoSection(
                currentUserUiState = currentUserUiState,
                suggestedIdeas = suggestedIdeas,
                ideasInProgress = ideasInProgress,
                implementedIdeas = implementedIdeas,
                officeEmployees = officeEmployees,
                isPullToRefreshActive = isRefreshing,
                onPostLikeClick = onPostLikeClick,
                onPostDislikeClick = onPostDislikeClick,
                onPostCommentsClick = onPostCommentsClick,
                onDeletePostClick = onDeletePostClick,
                navigateToIdeaDetailsScreen = navigateToIdeaDetailsScreen,
                navigateToEditIdeaScreen = navigateToEditIdeaScreen,
                navigateToAuthorScreen = navigateToAuthorScreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            )
        }
    }
}

@Composable
private fun IdeasAndEmployeesInfoSection(
    currentUserUiState: CurrentUserUiState,
    suggestedIdeas: LazyPagingItems<IdeaPost>,
    ideasInProgress: LazyPagingItems<IdeaPost>,
    implementedIdeas: LazyPagingItems<IdeaPost>,
    officeEmployees: LazyPagingItems<IdeaAuthor>,
    isPullToRefreshActive: Boolean,
    onPostLikeClick: (IdeaPost) -> Unit,
    onPostDislikeClick: (IdeaPost) -> Unit,
    onPostCommentsClick: (IdeaPost) -> Unit,
    onDeletePostClick: (IdeaPost) -> Unit,
    navigateToIdeaDetailsScreen: (Long) -> Unit,
    navigateToEditIdeaScreen: (Long) -> Unit,
    navigateToAuthorScreen: (authorId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentInfoGroup by remember { mutableStateOf(InfoGroup.IDEAS) }
    Column(
        verticalArrangement = Arrangement.spacedBy(40.dp),
        modifier = modifier
    ) {
        InfoGroupsSection(
            selected = currentInfoGroup,
            onIdeasClick = { currentInfoGroup = InfoGroup.IDEAS },
            onEmployeesClick = { currentInfoGroup = InfoGroup.EMPLOYEES },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.8f)
        )
        AnimatedIdeasGroupContent(
            visible = currentInfoGroup == InfoGroup.IDEAS,
            currentUserUiState = currentUserUiState,
            suggestedIdeas = suggestedIdeas,
            ideasInProgress = ideasInProgress,
            implementedIdeas = implementedIdeas,
            isPullToRefreshActive = isPullToRefreshActive,
            onPostLikeClick = onPostLikeClick,
            onPostDislikeClick = onPostDislikeClick,
            onPostCommentsClick = onPostCommentsClick,
            onDeletePostClick = onDeletePostClick,
            navigateToIdeaDetailsScreen = navigateToIdeaDetailsScreen,
            navigateToEditIdeaScreen = navigateToEditIdeaScreen,
            navigateToAuthorScreen = navigateToAuthorScreen
        )
        AnimatedEmployeesGroupContent(
            visible = currentInfoGroup == InfoGroup.EMPLOYEES,
            isPullToRefreshActive = isPullToRefreshActive,
            employees = officeEmployees,
            navigateToAuthorScreen = navigateToAuthorScreen
        )
    }
}

@Composable
fun AnimatedEmployeesGroupContent(
    visible: Boolean,
    isPullToRefreshActive: Boolean,
    employees: LazyPagingItems<IdeaAuthor>,
    navigateToAuthorScreen: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideIn(
            animationSpec = tween(),
            initialOffset = { IntOffset(x = it.width, y = 0) }
        ),
        exit = slideOut(
            animationSpec = tween(),
            targetOffset = { IntOffset(x = it.width, y = 0) }
        ),
        modifier = modifier
    ) {
        EmployeesGroupContent(
            employees = employees,
            isPullToRefreshActive = isPullToRefreshActive,
            navigateToAuthorScreen = navigateToAuthorScreen,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ColumnScope.AnimatedIdeasGroupContent(
    visible: Boolean,
    currentUserUiState: CurrentUserUiState,
    suggestedIdeas: LazyPagingItems<IdeaPost>,
    ideasInProgress: LazyPagingItems<IdeaPost>,
    implementedIdeas: LazyPagingItems<IdeaPost>,
    isPullToRefreshActive: Boolean,
    onPostLikeClick: (IdeaPost) -> Unit,
    onPostDislikeClick: (IdeaPost) -> Unit,
    onPostCommentsClick: (IdeaPost) -> Unit,
    onDeletePostClick: (IdeaPost) -> Unit,
    navigateToIdeaDetailsScreen: (Long) -> Unit,
    navigateToEditIdeaScreen: (Long) -> Unit,
    navigateToAuthorScreen: (authorId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideIn(
            animationSpec = tween(),
            initialOffset = { IntOffset(x = -it.width, y = 0) }
        ),
        exit = slideOut(
            animationSpec = tween(),
            targetOffset = { IntOffset(x = -it.width, y = 0) }
        ),
        modifier = modifier
    ) {
        IdeasGroupContent(
            currentUserUiState = currentUserUiState,
            suggestedIdeas = suggestedIdeas,
            ideasInProgress = ideasInProgress,
            implementedIdeas = implementedIdeas,
            isPullToRefreshActive = isPullToRefreshActive,
            onPostLikeClick = onPostLikeClick,
            onPostDislikeClick = onPostDislikeClick,
            onPostCommentsClick = onPostCommentsClick,
            onDeletePostClick = onDeletePostClick,
            navigateToIdeaDetailsScreen = navigateToIdeaDetailsScreen,
            navigateToEditIdeaScreen = navigateToEditIdeaScreen,
            navigateToAuthorScreen = navigateToAuthorScreen,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun EmployeesGroupContent(
    employees: LazyPagingItems<IdeaAuthor>,
    isPullToRefreshActive: Boolean,
    navigateToAuthorScreen: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyPagingItemsHorizontalGrid(
        items = employees,
        isPullToRefreshActive = isPullToRefreshActive,
        rows = GridCells.Fixed(2),
        itemKey = { it.id },
        itemsEmptyComposable = {},
        itemComposable = { employee ->
            EmployeeInfo(
                employee = employee,
                onClick = { navigateToAuthorScreen(employee.id) }
            )
        },
        errorWhileRefreshComposable = {
            ErrorScreen(
                message = stringResource(R.string.error_while_employees_loading),
                onRetryButtonClick = employees::retry
            )
        },
        errorWhileAppendComposable = {
            ErrorWhileAppending(
                message = stringResource(R.string.error_while_ideas_loading),
                onRetryButtonClick = employees::retry,
                modifier = Modifier.padding(10.dp)
            )
        },
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(26.dp),
        verticalArrangement = Arrangement.spacedBy(40.dp),
        modifier = modifier.height(200.dp)
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
private fun EmployeeInfo(
    employee: IdeaAuthor,
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
        AsyncImageWithLoading(
            model = employee.photo,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
        )
        Text(
            text = "${employee.name} ${employee.surname}",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun IdeasGroupContent(
    currentUserUiState: CurrentUserUiState,
    suggestedIdeas: LazyPagingItems<IdeaPost>,
    ideasInProgress: LazyPagingItems<IdeaPost>,
    implementedIdeas: LazyPagingItems<IdeaPost>,
    isPullToRefreshActive: Boolean,
    onPostLikeClick: (IdeaPost) -> Unit,
    onPostDislikeClick: (IdeaPost) -> Unit,
    onPostCommentsClick: (IdeaPost) -> Unit,
    onDeletePostClick: (IdeaPost) -> Unit,
    navigateToIdeaDetailsScreen: (Long) -> Unit,
    navigateToEditIdeaScreen: (Long) -> Unit,
    navigateToAuthorScreen: (authorId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
    ) {
        IdeasGroup.entries.forEach {
            val ideas = when (it) {
                IdeasGroup.SUGGESTED -> suggestedIdeas
                IdeasGroup.IN_PROGRESS -> ideasInProgress
                IdeasGroup.IMPLEMENTED -> implementedIdeas
            }
            IdeasContent(
                ideas = ideas,
                isPullToRefreshActive = isPullToRefreshActive,
                currentUserUiState = currentUserUiState,
                group = it,
                onPostLikeClick = onPostLikeClick,
                onPostDislikeClick = onPostDislikeClick,
                onPostCommentsClick = onPostCommentsClick,
                onDeletePostClick = onDeletePostClick,
                navigateToIdeaDetailsScreen = navigateToIdeaDetailsScreen,
                navigateToEditIdeaScreen = navigateToEditIdeaScreen,
                navigateToAuthorScreen = navigateToAuthorScreen,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun IdeasContent(
    ideas: LazyPagingItems<IdeaPost>,
    isPullToRefreshActive: Boolean,
    currentUserUiState: CurrentUserUiState,
    group: IdeasGroup,
    onPostLikeClick: (IdeaPost) -> Unit,
    onPostDislikeClick: (IdeaPost) -> Unit,
    onPostCommentsClick: (IdeaPost) -> Unit,
    onDeletePostClick: (IdeaPost) -> Unit,
    navigateToIdeaDetailsScreen: (Long) -> Unit,
    navigateToEditIdeaScreen: (Long) -> Unit,
    navigateToAuthorScreen: (authorId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    if (!expanded) {
        IdeasGroupHidden(
            group = group,
            onClick = { expanded = true },
            modifier = modifier
        )
    } else {
        IdeasGroupExpanded(
            ideas = ideas,
            isPullToRefreshActive = isPullToRefreshActive,
            currentUserUiState = currentUserUiState,
            group = group,
            onClick = { expanded = false },
            onPostLikeClick = onPostLikeClick,
            onPostDislikeClick = onPostDislikeClick,
            onPostCommentsClick = onPostCommentsClick,
            onDeletePostClick = onDeletePostClick,
            navigateToIdeaDetailsScreen = navigateToIdeaDetailsScreen,
            navigateToEditIdeaScreen = navigateToEditIdeaScreen,
            navigateToAuthorScreen = navigateToAuthorScreen,
            modifier = modifier
        )
    }
}

@Composable
private fun IdeasGroupExpanded(
    ideas: LazyPagingItems<IdeaPost>,
    isPullToRefreshActive: Boolean,
    currentUserUiState: CurrentUserUiState,
    group: IdeasGroup,
    onClick: () -> Unit,
    onPostLikeClick: (IdeaPost) -> Unit,
    onPostDislikeClick: (IdeaPost) -> Unit,
    onPostCommentsClick: (IdeaPost) -> Unit,
    onDeletePostClick: (IdeaPost) -> Unit,
    navigateToIdeaDetailsScreen: (Long) -> Unit,
    navigateToEditIdeaScreen: (Long) -> Unit,
    navigateToAuthorScreen: (authorId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .shadow(
                elevation = 3.dp,
                shape = MaterialTheme.shapes.large
            )
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
    ) {
        IdeasGroupExpandedTitle(
            group = group,
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 30.dp)
        )
        if (!ideas.isEmpty()) {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
        LazyPagingItemsColumn(
            items = ideas,
            isPullToRefreshActive = isPullToRefreshActive,
            itemKey = { it.id },
            itemsEmptyComposable = {},
            itemComposable = { post ->
                IdeaPost(
                    ideaPost = post,
                    isAuthorPostCurrentUser = post.ideaAuthor.id == currentUserUiState.user!!.id,
                    onPostClick = { navigateToIdeaDetailsScreen(post.id) },
                    onLikeClick = { onPostLikeClick(post) },
                    onDislikeClick = { onPostDislikeClick(post) },
                    onCommentClick = { onPostCommentsClick(post) },
                    navigateToAuthorScreen = navigateToAuthorScreen,
                    navigateToEditIdeaScreen = navigateToEditIdeaScreen,
                    onDeletePostClick = onDeletePostClick,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            errorWhileRefreshComposable = {
                ErrorScreen(
                    message = stringResource(R.string.error_while_ideas_loading),
                    onRetryButtonClick = ideas::retry,
                    modifier = Modifier.padding(20.dp)
                )
            },
            errorWhileAppendComposable = {
                ErrorWhileAppending(
                    message = stringResource(R.string.error_while_ideas_loading),
                    onRetryButtonClick = ideas::retry,
                    modifier = Modifier.padding(10.dp)
                )
            },
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ideasExpandedAspectRation(ideas.itemCount))
        )
    }
}

@Composable
private fun IdeasGroupExpandedTitle(
    group: IdeasGroup,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Text(
            text = ideasGroupName(ideasGroup = group),
            style = MaterialTheme.typography.bodySmall,
            fontSize = 20.sp
        )
        Icon(
            painter = painterResource(R.drawable.baseline_keyboard_arrow_down_24),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "arrow_right",
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
private fun IdeasGroupHidden(
    group: IdeasGroup,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .shadow(
                elevation = 3.dp,
                shape = MaterialTheme.shapes.large
            )
            .background(MaterialTheme.colorScheme.background)
            .clip(MaterialTheme.shapes.large)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 16.dp, horizontal = 30.dp)
    ) {
        Text(
            text = ideasGroupName(ideasGroup = group),
            style = MaterialTheme.typography.bodySmall,
            fontSize = 20.sp
        )
        Icon(
            painter = painterResource(R.drawable.baseline_keyboard_arrow_right_24),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "arrow_right",
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
private fun OfficeInfo(
    office: Office,
    modifier: Modifier = Modifier
) {
    AsyncImageWithLoading(
        model = office.imageUrl,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium
            )
    )
    Text(
        text = office.address,
        style = MaterialTheme.typography.bodyMedium,
        fontSize = 16.sp,
        modifier = Modifier.padding(top = 10.dp)
    )
}

@Composable
private fun Title(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.my_office),
        style = MaterialTheme.typography.titleLarge,
        fontSize = 32.sp,
        modifier = modifier
    )
}

@Composable
private fun InfoGroupsSection(
    selected: InfoGroup,
    onIdeasClick: () -> Unit,
    onEmployeesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    Column(modifier = modifier) {
        var currentDividerWidth by remember { mutableStateOf(0.dp) }
        var currentDividerXOffset by remember { mutableFloatStateOf(0f) }
        val animatedDividerOffset by animateDpAsState(
            targetValue = with(density) { currentDividerXOffset.toDp() },
            animationSpec = tween(),
            label = "divider_animation_offset"
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IdeasGroupTitle(
                onClick = onIdeasClick,
                modifier = Modifier
                    .conditional(selected == InfoGroup.IDEAS) {
                        onGloballyPositioned {
                            currentDividerWidth = with(density) { it.size.width.toDp() }
                            currentDividerXOffset = it.positionInParent().x
                        }
                    }
            )
            EmployeesGroupTitle(
                onClick = onEmployeesClick,
                modifier = Modifier
                    .conditional(selected == InfoGroup.EMPLOYEES) {
                        onGloballyPositioned {
                            currentDividerWidth = with(density) { it.size.width.toDp() }
                            currentDividerXOffset = it.positionInParent().x
                        }
                    }
            )
        }
        HorizontalDivider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .requiredWidth(currentDividerWidth)
                .offset(x = animatedDividerOffset)
        )
    }
}

@Composable
private fun IdeasGroupTitle(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.ideas),
        style = MaterialTheme.typography.bodyLarge,
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(10.dp)
    )
}

@Composable
private fun EmployeesGroupTitle(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.workers),
        style = MaterialTheme.typography.bodyLarge,
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(10.dp)
    )
}

@Composable
private fun ideasGroupName(ideasGroup: IdeasGroup) =
    when (ideasGroup) {
        IdeasGroup.SUGGESTED -> stringResource(R.string.ideas_suggested)
        IdeasGroup.IN_PROGRESS -> stringResource(R.string.ideas_in_progress)
        IdeasGroup.IMPLEMENTED -> stringResource(R.string.ideas_implemented)
    }

private fun isScreenLoading(currentUserUiState: CurrentUserUiState) = currentUserUiState.isLoading

private fun isErrorWhileLoading(currentUserUiState: CurrentUserUiState) =
    currentUserUiState.isErrorWhileLoading

private fun ideasExpandedAspectRation(ideasCount: Int) =
    1f / min(ideasCount, 3).coerceAtLeast(1)