package ru.ilyasekunov.ideaauthor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import ru.ilyasekunov.model.IdeaAuthor
import ru.ilyasekunov.model.IdeaPost
import ru.ilyasekunov.model.Office
import ru.ilyasekunov.navigation.BottomNavigationScreen
import ru.ilyasekunov.officeapp.feature.ideaauthor.R
import ru.ilyasekunov.profile.UserInfoSection
import ru.ilyasekunov.ui.AnimatedLoadingScreen
import ru.ilyasekunov.ui.ErrorScreen
import ru.ilyasekunov.ui.LoadingScreen
import ru.ilyasekunov.ui.RetryButton
import ru.ilyasekunov.ui.components.AsyncImageWithLoading
import ru.ilyasekunov.ui.components.BothDirectedPullToRefreshContainer
import ru.ilyasekunov.ui.components.BottomNavigationBar
import ru.ilyasekunov.ui.components.LikesAndDislikesSection
import ru.ilyasekunov.ui.components.NavigateBackArrow
import ru.ilyasekunov.ui.components.defaultNavigateBackArrowScrollBehaviour
import ru.ilyasekunov.ui.isAppending
import ru.ilyasekunov.ui.isEmpty
import ru.ilyasekunov.ui.isErrorWhileAppending
import ru.ilyasekunov.ui.isErrorWhileRefreshing
import ru.ilyasekunov.ui.isRefreshing
import ru.ilyasekunov.ui.theme.OfficeAppTheme
import ru.ilyasekunov.util.toRussianString
import java.time.LocalDateTime
import ru.ilyasekunov.officeapp.core.ui.R as coreUiResources

@Composable
fun IdeaAuthorScreen(
    ideaAuthorUiState: IdeaAuthorUiState,
    ideas: LazyPagingItems<IdeaPost>,
    onRetryLoadData: () -> Unit,
    onPullToRefresh: CoroutineScope.() -> Job,
    onIdeaLikeClick: (idea: IdeaPost) -> Unit,
    onIdeaDislikeClick: (idea: IdeaPost) -> Unit,
    navigateToIdeaDetailsScreen: (Long) -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = BottomNavigationScreen.Home,
                navigateToHomeScreen = navigateToHomeScreen,
                navigateToFavouriteScreen = navigateToFavouriteScreen,
                navigateToMyOfficeScreen = navigateToMyOfficeScreen,
                navigateToProfileScreen = navigateToProfileScreen,
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) { paddingValues ->
        when {
            isScreenLoading(ideaAuthorUiState) -> AnimatedLoadingScreen()
            isErrorWhileLoading(ideaAuthorUiState) -> {
                ErrorScreen(
                    message = stringResource(coreUiResources.string.core_ui_error_connecting_to_server),
                    onRetryButtonClick = onRetryLoadData
                )
            }

            !ideaAuthorUiState.isExists -> AuthorNotExistsScreen()
            else -> IdeaAuthorScreenContent(
                ideaAuthorUiState = ideaAuthorUiState,
                ideas = ideas,
                onPullToRefresh = onPullToRefresh,
                onIdeaLikeClick = onIdeaLikeClick,
                onIdeaDislikeClick = onIdeaDislikeClick,
                navigateToIdeaDetailsScreen = navigateToIdeaDetailsScreen,
                navigateBack = navigateBack,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
            )
        }
    }
}

@Composable
private fun IdeaAuthorScreenContent(
    ideaAuthorUiState: IdeaAuthorUiState,
    ideas: LazyPagingItems<IdeaPost>,
    onPullToRefresh: CoroutineScope.() -> Job,
    onIdeaLikeClick: (idea: IdeaPost) -> Unit,
    onIdeaDislikeClick: (idea: IdeaPost) -> Unit,
    navigateToIdeaDetailsScreen: (Long) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navigateBackArrowScrollBehaviour = defaultNavigateBackArrowScrollBehaviour()
    BothDirectedPullToRefreshContainer(
        onRefreshTrigger = onPullToRefresh,
        modifier = modifier.nestedScroll(navigateBackArrowScrollBehaviour.nestedScrollConnection)
    ) { isRefreshing ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                UserInfoSection(
                    name = ideaAuthorUiState.name,
                    surname = ideaAuthorUiState.surname,
                    photoUrl = ideaAuthorUiState.photoUrl,
                    job = ideaAuthorUiState.job,
                    containerColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OfficeSection(
                    office = ideaAuthorUiState.office!!,
                    officeImageSize = DpSize(height = 280.dp, width = 280.dp),
                    modifier = Modifier.padding(vertical = 18.dp)
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 15.dp)
                )
            }
            item {
                Text(
                    text = stringResource(coreUiResources.string.core_ui_ideas),
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 26.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                )
            }
            ideas(
                ideas = ideas,
                isPullToRefreshActive = isRefreshing,
                onIdeaLikeClick = onIdeaLikeClick,
                onIdeaDislikeClick = onIdeaDislikeClick,
                navigateToIdeaDetailsScreen = navigateToIdeaDetailsScreen
            )
        }
        NavigateBackArrow(
            onClick = navigateBack,
            scrollBehaviour = navigateBackArrowScrollBehaviour,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 10.dp)
        )
    }
}

private fun LazyListScope.ideas(
    ideas: LazyPagingItems<IdeaPost>,
    isPullToRefreshActive: Boolean,
    onIdeaLikeClick: (idea: IdeaPost) -> Unit,
    onIdeaDislikeClick: (idea: IdeaPost) -> Unit,
    navigateToIdeaDetailsScreen: (Long) -> Unit
) {
    when {
        ideas.isRefreshing() && !isPullToRefreshActive -> {
            item { LoadingScreen() }
        }

        ideas.isErrorWhileRefreshing() -> {
            item {
                ErrorScreen(
                    message = stringResource(coreUiResources.string.core_ui_error_while_ideas_loading),
                    onRetryButtonClick = ideas::retry
                )
            }
        }

        ideas.isEmpty() -> {
            item {
                Text(
                    text = stringResource(coreUiResources.string.core_ui_list_is_empty_yet),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        else -> {
            items(
                count = ideas.itemCount,
                key = ideas.itemKey { it.id }
            ) {
                val idea = ideas[it]!!
                Idea(
                    idea = idea,
                    onIdeaClick = { navigateToIdeaDetailsScreen(idea.id) },
                    onLikeClick = { onIdeaLikeClick(idea) },
                    onDislikeClick = { onIdeaDislikeClick(idea) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (ideas.isAppending()) {
                item { LoadingScreen() }
            }
            if (ideas.isErrorWhileAppending()) {
                item {
                    ErrorWhileAppending(
                        message = stringResource(coreUiResources.string.core_ui_error_while_ideas_loading),
                        onRetryButtonClick = ideas::retry,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
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
private fun AuthorNotExistsScreen(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.feature_ideaauthor_user_not_exists),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun OfficeSection(
    office: Office,
    officeImageSize: DpSize,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = stringResource(coreUiResources.string.core_ui_office),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(18.dp))
        AsyncImageWithLoading(
            model = office.imageUrl,
            modifier = Modifier
                .size(officeImageSize)
                .clip(MaterialTheme.shapes.medium)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                )
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = office.address,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun Idea(
    idea: IdeaPost,
    onIdeaClick: () -> Unit,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 1.dp,
                shape = MaterialTheme.shapes.large
            )
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onIdeaClick)
            .background(MaterialTheme.colorScheme.background)
            .padding(15.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = idea.title,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
                fontSize = 16.sp
            )
            LikesAndDislikesSection(
                isLikePressed = idea.isLikePressed,
                likesCount = idea.likesCount,
                onLikeClick = onLikeClick,
                isDislikePressed = idea.isDislikePressed,
                dislikesCount = idea.dislikesCount,
                onDislikeClick = onDislikeClick,
                spaceBetweenCategories = 20.dp,
                likesIconSize = 18.dp,
                dislikesIconSize = 18.dp,
                textSize = 14.sp,
                buttonsWithBackground = true,
                buttonsWithRippleEffect = true
            )
        }
        Text(
            text = idea.date.toRussianString(),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

private fun isScreenLoading(ideaAuthorUiState: IdeaAuthorUiState) = ideaAuthorUiState.isLoading

private fun isErrorWhileLoading(ideaAuthorUiState: IdeaAuthorUiState) =
    ideaAuthorUiState.isErrorWhileLoading

@Preview
@Composable
private fun IdeaPreview() {
    OfficeAppTheme {
        Surface {
            Idea(
                idea = IdeaPost(
                    id = 0,
                    title = "Растительнае вставки для стола",
                    content = "",
                    date = LocalDateTime.now(),
                    ideaAuthor = IdeaAuthor(
                        id = 0,
                        name = "",
                        surname = "",
                        job = "",
                        photo = "",
                        office = Office(
                            id = 1,
                            imageUrl = "",
                            address = ""
                        )
                    ),
                    attachedImages = emptyList(),
                    office = Office(
                        id = 0,
                        imageUrl = "",
                        address = ""
                    ),
                    likesCount = 1139,
                    isLikePressed = true,
                    dislikesCount = 101,
                    isDislikePressed = false,
                    commentsCount = 0,
                    isInProgress = false,
                    isImplemented = false,
                    isSuggestedToMyOffice = false
                ),
                onIdeaClick = {},
                onLikeClick = {},
                onDislikeClick = {}
            )
        }
    }
}