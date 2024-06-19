package ru.ilyasekunov.favouriteideas

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
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import ru.ilyasekunov.filters.FiltersUiState
import ru.ilyasekunov.filters.OfficeFilterUiState
import ru.ilyasekunov.home.HomeAppBar
import ru.ilyasekunov.home.SearchUiState
import ru.ilyasekunov.model.IdeaPost
import ru.ilyasekunov.officeapp.feature.favouriteideas.R
import ru.ilyasekunov.ui.AnimatedLoadingScreen
import ru.ilyasekunov.ui.ErrorScreen
import ru.ilyasekunov.ui.LoadingScreen
import ru.ilyasekunov.ui.LocalCurrentNavigationBarScreen
import ru.ilyasekunov.ui.components.AsyncImageWithLoading
import ru.ilyasekunov.ui.components.BothDirectedPullToRefreshContainer
import ru.ilyasekunov.ui.components.BottomNavigationBar
import ru.ilyasekunov.ui.components.LazyPagingItemsVerticalGrid
import kotlin.random.Random
import kotlin.random.nextInt
import ru.ilyasekunov.officeapp.core.ui.R.string as coreUiStrings

@Composable
fun FavouriteIdeasScreen(
    favouriteIdeas: LazyPagingItems<IdeaPost>,
    filtersUiState: FiltersUiState,
    onOfficeFilterRemoveClick: (OfficeFilterUiState) -> Unit,
    searchUiState: SearchUiState,
    ideasGridState: LazyGridState = rememberLazyGridState(),
    onSearchValueChange: (String) -> Unit,
    onSortingFilterRemoveClick: () -> Unit,
    onRetryInfoLoad: () -> Unit,
    onPullToRefresh: CoroutineScope.() -> Job,
    navigateToFiltersScreen: () -> Unit,
    navigateToIdeaDetailsScreen: (postId: Long) -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
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
                navigateToFavouriteScreen = navigateToFavouriteScreen,
                navigateToMyOfficeScreen = navigateToMyOfficeScreen,
                navigateToProfileScreen = navigateToProfileScreen,
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        when {
            isScreenLoading(filtersUiState) -> AnimatedLoadingScreen()
            isErrorWhileLoading(filtersUiState) -> {
                ErrorScreen(
                    message = stringResource(coreUiStrings.core_ui_error_connecting_to_server),
                    onRetryButtonClick = onRetryInfoLoad
                )
            }

            else -> {
                BothDirectedPullToRefreshContainer(
                    onRefreshTrigger = onPullToRefresh,
                    modifier = Modifier.padding(paddingValues)
                ) { isRefreshing ->
                    FavouriteIdeas(
                        favouriteIdeas = favouriteIdeas,
                        lazyGridState = ideasGridState,
                        isPullToRefreshActive = isRefreshing,
                        favouriteIdeaSize = 100.dp,
                        onIdeaClick = navigateToIdeaDetailsScreen,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun FavouriteIdeas(
    favouriteIdeas: LazyPagingItems<IdeaPost>,
    isPullToRefreshActive: Boolean,
    favouriteIdeaSize: Dp,
    onIdeaClick: (postId: Long) -> Unit,
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState()
) {
    LazyPagingItemsVerticalGrid(
        items = favouriteIdeas,
        lazyGridState = lazyGridState,
        isPullToRefreshActive = isPullToRefreshActive,
        columns = GridCells.Adaptive(minSize = favouriteIdeaSize),
        itemKey = { it.id },
        itemsEmptyComposable = { NoFavouriteIdeas() },
        itemComposable = { idea ->
            FavouriteIdea(
                ideaPost = idea,
                onClick = { onIdeaClick(idea.id) },
                modifier = Modifier
                    .fillMaxSize()
                    .size(favouriteIdeaSize)
            )
        },
        itemsAppendComposable = {
            LoadingScreen(modifier = Modifier.size(favouriteIdeaSize))
        },
        errorWhileRefreshComposable = {
            ErrorScreen(
                message = stringResource(coreUiStrings.core_ui_error_while_ideas_loading),
                onRetryButtonClick = favouriteIdeas::retry
            )
        },
        errorWhileAppendComposable = {
            ErrorWhileAppending(
                message = stringResource(coreUiStrings.core_ui_error_while_ideas_loading),
                modifier = Modifier.size(favouriteIdeaSize)
            )
        },
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
    )
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
                text = stringResource(R.string.feature_favouriteideas_favourite_ideas),
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
        text = stringResource(R.string.feature_favouriteideas_no_favourites_ideas),
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
fun FavouriteIdea(
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

private fun isScreenLoading(filtersUiState: FiltersUiState) = filtersUiState.isLoading
private fun isErrorWhileLoading(filtersUiState: FiltersUiState) = filtersUiState.isErrorWhileLoading