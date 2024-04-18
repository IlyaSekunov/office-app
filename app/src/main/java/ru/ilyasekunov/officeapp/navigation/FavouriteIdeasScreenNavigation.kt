package ru.ilyasekunov.officeapp.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
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
        val favouriteIdeas = viewModel.favouriteIdeasUiState.collectAsLazyPagingItems()
        FavouriteIdeasScreen(
            favouriteIdeas = favouriteIdeas,
            filtersUiState = viewModel.filtersUiStateHolder.filtersUiState,
            onOfficeFilterRemoveClick = viewModel.filtersUiStateHolder::removeOfficeFilter,
            searchUiState = viewModel.searchUiState,
            onSearchValueChange = viewModel::updateSearchValue,
            onSortingFilterRemoveClick = viewModel.filtersUiStateHolder::removeSortingFilter,
            onRetryInfoLoad = viewModel::loadFavouritePosts,
            onPullToRefresh = favouriteIdeas::refresh,
            navigateToFiltersScreen = navigateToFiltersScreen,
            navigateToIdeaDetailsScreen = navigateToIdeaDetailsScreen,
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToMyOfficeScreen = navigateToMyOfficeScreen,
            navigateToProfileScreen = navigateToProfileScreen
        )
    }
}

fun NavController.navigateToFavouriteIdeasScreen(navOptions: NavOptions? = null) =
    navigate(BottomNavigationScreen.Favourite.route, navOptions)