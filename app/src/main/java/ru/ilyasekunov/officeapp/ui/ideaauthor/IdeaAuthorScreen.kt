package ru.ilyasekunov.officeapp.ui.ideaauthor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.valentinilk.shimmer.shimmer
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.navigation.BottomNavigationScreen
import ru.ilyasekunov.officeapp.ui.ErrorScreen
import ru.ilyasekunov.officeapp.ui.LoadingScreen
import ru.ilyasekunov.officeapp.ui.components.BasicPullToRefreshContainer
import ru.ilyasekunov.officeapp.ui.components.BottomNavigationBar
import ru.ilyasekunov.officeapp.ui.components.NavigateBackArrow
import ru.ilyasekunov.officeapp.ui.components.defaultNavigateBackArrowScrollBehaviour
import ru.ilyasekunov.officeapp.ui.home.DislikeButton
import ru.ilyasekunov.officeapp.ui.home.LikeButton
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme
import ru.ilyasekunov.officeapp.ui.userprofile.UserInfoSection
import ru.ilyasekunov.officeapp.util.toRussianString
import java.time.LocalDateTime

@Composable
fun IdeaAuthorScreen(
    ideaAuthorUiState: IdeaAuthorUiState,
    ideas: LazyPagingItems<IdeaPost>,
    onRetryLoadData: () -> Unit,
    onPullToRefresh: suspend () -> Unit,
    onIdeaLikeClick: (idea: IdeaPost, isPressed: Boolean) -> Unit,
    onIdeaDislikeClick: (idea: IdeaPost, isPressed: Boolean) -> Unit,
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
        val ideasLoading = ideas.loadState.refresh == LoadState.Loading
        when {
            ideaAuthorUiState.isLoading || ideasLoading -> LoadingScreen()
            ideaAuthorUiState.isErrorWhileLoading || ideas.loadState.hasError -> {
                ErrorScreen(
                    message = stringResource(R.string.error_connecting_to_server),
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
                modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
            )
        }
    }
}

@Composable
fun IdeaAuthorScreenContent(
    ideaAuthorUiState: IdeaAuthorUiState,
    ideas: LazyPagingItems<IdeaPost>,
    onPullToRefresh: suspend () -> Unit,
    onIdeaLikeClick: (idea: IdeaPost, isPressed: Boolean) -> Unit,
    onIdeaDislikeClick: (idea: IdeaPost, isPressed: Boolean) -> Unit,
    navigateToIdeaDetailsScreen: (Long) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicPullToRefreshContainer(onRefreshTrigger = onPullToRefresh) {
        Box {
            val navigateBackArrowScrollBehaviour = defaultNavigateBackArrowScrollBehaviour()
            val authorPhotoTopPadding = 15.dp
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
                    .fillMaxSize()
                    .nestedScroll(navigateBackArrowScrollBehaviour.nestedScrollConnection)
            ) {
                item {
                    IdeaAuthorSection(
                        ideaAuthorUiState = ideaAuthorUiState,
                        contentTopPadding = authorPhotoTopPadding
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(R.string.ideas),
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 26.sp
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                }
                if (ideas.itemCount == 0) {
                    item {
                        Text(
                            text = stringResource(R.string.list_is_empty_yet),
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                } else {
                    items(
                        count = ideas.itemCount,
                        key = ideas.itemKey { it.id }
                    ) {
                        val idea = ideas[it]!!
                        Idea(
                            idea = idea,
                            onIdeaClick = { navigateToIdeaDetailsScreen(idea.id) },
                            onLikeClick = { onIdeaLikeClick(idea, !idea.isLikePressed) },
                            onDislikeClick = {
                                onIdeaDislikeClick(
                                    idea,
                                    !idea.isDislikePressed
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
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
}

@Composable
fun IdeaAuthorSection(
    ideaAuthorUiState: IdeaAuthorUiState,
    contentTopPadding: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        UserInfoSection(
            name = ideaAuthorUiState.name,
            surname = ideaAuthorUiState.surname,
            photoUrl = ideaAuthorUiState.photoUrl,
            job = ideaAuthorUiState.job,
            contentTopPadding = contentTopPadding,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        )
        Spacer(modifier = Modifier.height(18.dp))
        if (ideaAuthorUiState.office != null) {
            OfficeSection(
                office = ideaAuthorUiState.office,
                officeImageSize = DpSize(height = 280.dp, width = 280.dp)
            )
            Spacer(modifier = Modifier.height(18.dp))
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(horizontal = 15.dp)
        )
    }
}

@Composable
fun AuthorNotExistsScreen(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.user_not_exists),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun OfficeSection(
    office: Office,
    officeImageSize: DpSize,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.office),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(18.dp))
        val asyncImagePainter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(office.imageUrl)
                .size(coil.size.Size.ORIGINAL)
                .build()
        )
        val officeImageModifier = Modifier
            .size(officeImageSize)
            .clip(MaterialTheme.shapes.medium)
        when (asyncImagePainter.state) {
            is AsyncImagePainter.State.Loading -> {
                Box(modifier = officeImageModifier.shimmer())
            }

            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = asyncImagePainter,
                    contentDescription = "office_image",
                    contentScale = ContentScale.Crop,
                    modifier = officeImageModifier
                        .border(
                            width = 1.dp,
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = office.address,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp
                )
            }

            else -> {}
        }
    }
}

@Composable
fun Idea(
    idea: IdeaPost,
    onIdeaClick: () -> Unit,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onIdeaClick)
            .background(MaterialTheme.colorScheme.background)
            .padding(15.dp)
    ) {
        Column {
            Text(
                text = idea.title,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                LikeButton(
                    onClick = onLikeClick,
                    iconSize = 18.dp,
                    isPressed = idea.isLikePressed,
                    count = idea.likesCount
                )
                Spacer(modifier = Modifier.width(20.dp))
                DislikeButton(
                    onClick = onDislikeClick,
                    iconSize = 18.dp,
                    isPressed = idea.isDislikePressed,
                    count = idea.dislikesCount
                )
            }
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
                    commentsCount = 0
                ),
                onIdeaClick = {},
                onLikeClick = {},
                onDislikeClick = {}
            )
        }
    }
}