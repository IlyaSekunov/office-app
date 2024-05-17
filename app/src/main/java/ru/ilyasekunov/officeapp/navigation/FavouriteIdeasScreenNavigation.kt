package ru.ilyasekunov.officeapp.navigation

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.ui.favouriteideas.FavouriteIdeasScreen
import ru.ilyasekunov.officeapp.ui.favouriteideas.FavouriteIdeasViewModel

fun NavGraphBuilder.favouriteIdeasScreen(
    navigateToFiltersScreen: () -> Unit,
    navigateToIdeaDetailsScreen: (postId: Long) -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit
) {
    composable(route = BottomNavigationScreen.Favourite.route) {
        val viewModel = hiltViewModel<FavouriteIdeasViewModel>()
        val favouriteIdeas = viewModel.favouriteIdeasUiState.data.collectAsLazyPagingItems()
        val ideasGridState = rememberLazyGridState()
        val coroutineScope = rememberCoroutineScope()
        FavouriteIdeasScreen(
            favouriteIdeas = favouriteIdeas,
            filtersUiState = viewModel.filtersUiStateHolder.filtersUiState,
            onOfficeFilterRemoveClick = viewModel.filtersUiStateHolder::removeOfficeFilter,
            searchUiState = viewModel.searchUiState,
            onSearchValueChange = viewModel::updateSearchValue,
            onSortingFilterRemoveClick = viewModel.filtersUiStateHolder::removeSortingFilter,
            onRetryInfoLoad = {
                viewModel.loadData()
                favouriteIdeas.retry()
            },
            onPullToRefresh = { launch { favouriteIdeas.refresh() } },
            navigateToFiltersScreen = navigateToFiltersScreen,
            navigateToIdeaDetailsScreen = navigateToIdeaDetailsScreen,
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToFavouriteScreen = {
                coroutineScope.launch {
                    ideasGridState.scrollToItem(0)
                }
            },
            navigateToMyOfficeScreen = navigateToMyOfficeScreen,
            navigateToProfileScreen = navigateToProfileScreen
        )
    }
}

fun NavController.navigateToFavouriteIdeasScreen(navOptions: NavOptions? = null) =
    navigate(BottomNavigationScreen.Favourite.route, navOptions)