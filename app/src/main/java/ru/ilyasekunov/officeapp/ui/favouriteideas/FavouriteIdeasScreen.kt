package ru.ilyasekunov.officeapp.ui.favouriteideas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.ui.AnimatedLoadingScreen
import ru.ilyasekunov.officeapp.ui.ErrorScreen
import ru.ilyasekunov.officeapp.ui.LocalCurrentNavigationBarScreen
import ru.ilyasekunov.officeapp.ui.components.AsyncImageWithLoading
import ru.ilyasekunov.officeapp.ui.components.BasicPullToRefreshContainer
import ru.ilyasekunov.officeapp.ui.components.BottomNavigationBar
import ru.ilyasekunov.officeapp.ui.filters.FiltersUiState
import ru.ilyasekunov.officeapp.ui.home.HomeAppBar
import ru.ilyasekunov.officeapp.ui.home.OfficeFilterUiState
import ru.ilyasekunov.officeapp.ui.home.SearchUiState
import ru.ilyasekunov.officeapp.ui.theme.favouriteIdeaColorBlue
import ru.ilyasekunov.officeapp.ui.theme.favouriteIdeaColorGreen
import ru.ilyasekunov.officeapp.ui.theme.favouriteIdeaColorOrange
import ru.ilyasekunov.officeapp.ui.theme.favouriteIdeaColorPurple
import ru.ilyasekunov.officeapp.ui.theme.favouriteIdeaColorRed
import ru.ilyasekunov.officeapp.ui.theme.favouriteIdeaColorYellow
import ru.ilyasekunov.officeapp.util.isAppending
import ru.ilyasekunov.officeapp.util.isEmpty
import ru.ilyasekunov.officeapp.util.isError
import ru.ilyasekunov.officeapp.util.isRefreshing
import kotlin.random.Random
import kotlin.random.nextInt

private val favouriteIdeaColors = listOf(
    favouriteIdeaColorBlue,
    favouriteIdeaColorGreen,
    favouriteIdeaColorOrange,
    favouriteIdeaColorPurple,
    favouriteIdeaColorRed,
    favouriteIdeaColorYellow
)

@Composable
fun FavouriteIdeasScreen(
    favouriteIdeas: LazyPagingItems<IdeaPost>,
    filtersUiState: FiltersUiState,
    onOfficeFilterRemoveClick: (OfficeFilterUiState) -> Unit,
    searchUiState: SearchUiState,
    onSearchValueChange: (String) -> Unit,
    onSortingFilterRemoveClick: () -> Unit,
    onRetryInfoLoad: () -> Unit,
    onPullToRefresh: CoroutineScope.() -> Job,
    navigateToFiltersScreen: () -> Unit,
    navigateToIdeaDetailsScreen: (postId: Long) -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit
) {
    Scaffold(
        topBar = {
            FavouriteIdeasTopAppBar(
                filtersUiState = filtersUiState,
                onOfficeFilterRemoveClick = onOfficeFilterRemoveClick,
                searchUiState = searchUiState,
                onSearchValueChange = onSearchValueChange,
                onSortingFilterRemoveClick = onSortingFilterRemoveClick,
                navigateToFiltersScreen = navigateToFiltersScreen,
                modifier = Modifier.fillMaxWidth()
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = LocalCurrentNavigationBarScreen.current,
                navigateToHomeScreen = navigateToHomeScreen,
                navigateToMyOfficeScreen = navigateToMyOfficeScreen,
                navigateToProfileScreen = navigateToProfileScreen,
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        when {
            isScreenLoading(favouriteIdeas, filtersUiState) -> AnimatedLoadingScreen()
            isErrorWhileLoading(favouriteIdeas, filtersUiState) -> {
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
                    if (favouriteIdeas.isEmpty()) {
                        NoFavouriteIdeas(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        )
                    } else {
                        FavouriteIdeas(
                            favouriteIdeas = favouriteIdeas,
                            favouriteIdeaSize = 100.dp,
                            onIdeaClick = navigateToIdeaDetailsScreen,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavouriteIdeasTopAppBar(
    filtersUiState: FiltersUiState,
    onOfficeFilterRemoveClick: (OfficeFilterUiState) -> Unit,
    searchUiState: SearchUiState,
    onSearchValueChange: (String) -> Unit,
    onSortingFilterRemoveClick: () -> Unit,
    navigateToFiltersScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    HomeAppBar(
        searchUiState = searchUiState,
        onSearchValueChange = onSearchValueChange,
        filtersUiState = filtersUiState,
        onOfficeFilterRemoveClick = onOfficeFilterRemoveClick,
        onSortingFilterRemoveClick = onSortingFilterRemoveClick,
        onFiltersClick = navigateToFiltersScreen,
        modifier = modifier,
        title = { paddingValues ->
            Text(
                text = stringResource(R.string.favourite_ideas),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }
    )
}

@Composable
private fun NoFavouriteIdeas(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.no_favourites_ideas),
        style = MaterialTheme.typography.bodyLarge,
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.wrapContentSize(Alignment.Center)
    )
}

@Composable
private fun FavouriteIdeas(
    favouriteIdeas: LazyPagingItems<IdeaPost>,
    favouriteIdeaSize: Dp,
    onIdeaClick: (postId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = favouriteIdeaSize),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
    ) {
        items(
            count = favouriteIdeas.itemCount,
            key = favouriteIdeas.itemKey { it.id }
        ) {
            val idea = favouriteIdeas[it]!!
            FavouriteIdea(
                ideaPost = idea,
                onClick = { onIdeaClick(idea.id) },
                modifier = Modifier
                    .fillMaxSize()
                    .size(favouriteIdeaSize)
            )
        }
        if (favouriteIdeas.isAppending()) {
            item {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp,
                    modifier = Modifier
                        .size(favouriteIdeaSize)
                        .wrapContentSize(Alignment.Center)
                        .size(30.dp)
                )
            }
        }
    }
}

@Composable
private fun FavouriteIdea(
    ideaPost: IdeaPost,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (ideaPost.attachedImages.isNotEmpty()) {
        AsyncImageWithLoading(
            model = ideaPost.attachedImages.first(),
            modifier = modifier
                .fillMaxSize()
                .clickable(onClick = onClick)
        )
    } else {
        val color = rememberRandomFavouriteIdeaColor()
        Text(
            text = ideaPost.title,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = modifier
                .clickable(onClick = onClick)
                .background(color)
                .wrapContentSize(Alignment.Center)
                .padding(4.dp)
        )
    }
}

@Composable
fun rememberRandomFavouriteIdeaColor() = rememberSaveable(saver = ColorSaver) {
    val range = favouriteIdeaColors.indices
    favouriteIdeaColors[Random.nextInt(range)]
}

val ColorSaver = listSaver(
    save = { listOf(it.red, it.green, it.blue) },
    restore = {
        Color(
            red = it[0] as Float,
            green = it[1] as Float,
            blue = it[2] as Float
        )
    }
)

private fun isScreenLoading(
    favouriteIdeas: LazyPagingItems<IdeaPost>,
    filtersUiState: FiltersUiState
) = favouriteIdeas.isRefreshing() || filtersUiState.isLoading


private fun isErrorWhileLoading(
    favouriteIdeas: LazyPagingItems<IdeaPost>,
    filtersUiState: FiltersUiState
) = favouriteIdeas.isError() || filtersUiState.isErrorWhileLoading