package ru.ilyasekunov.officeapp.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import ru.ilyasekunov.officeapp.navigation.BottomNavigationScreen
import ru.ilyasekunov.officeapp.ui.home.HomeScreen
import ru.ilyasekunov.officeapp.ui.home.HomeViewModel

fun NavGraphBuilder.homeScreen(
    navigateToIdeaDetailsScreen: (postId: Long, initiallyScrollToComments: Boolean) -> Unit,
    navigateToSuggestIdeaScreen: () -> Unit,
    navigateToFiltersScreen: () -> Unit,
    navigateToAuthorScreen: (authorId: Long) -> Unit,
    navigateToEditIdeaScreen: (postId: Long) -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateToAuthGraph: () -> Unit
) {
    composable(route = BottomNavigationScreen.Home.route) {
        val viewModel = hiltViewModel<HomeViewModel>()
        val posts = viewModel.postsUiState.collectAsLazyPagingItems()
        HomeScreen(
            posts = posts,
            currentUserUiState = viewModel.currentUserUiState,
            searchUiState = viewModel.searchUiState,
            onSearchValueChange = viewModel::updateSearchValue,
            filtersUiState = viewModel.filtersUiStateHolder.filtersUiState,
            onOfficeFilterRemoveClick = viewModel.filtersUiStateHolder::removeOfficeFilter,
            onSortingFilterRemoveClick = viewModel.filtersUiStateHolder::removeSortingFilter,
            onDeletePostClick = viewModel::deletePost,
            onPostLikeClick = viewModel::updateLike,
            onPostDislikeClick = viewModel::updateDislike,
            onRetryInfoLoad = {
                viewModel.loadCurrentUser()
                viewModel.loadPosts()
            },
            onPullToRefresh = posts::refresh,
            navigateToFiltersScreen = navigateToFiltersScreen,
            navigateToSuggestIdeaScreen = navigateToSuggestIdeaScreen,
            navigateToIdeaDetailsScreen = navigateToIdeaDetailsScreen,
            navigateToAuthorScreen = navigateToAuthorScreen,
            navigateToEditIdeaScreen = navigateToEditIdeaScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToMyOfficeScreen = navigateToMyOfficeScreen,
            navigateToProfileScreen = navigateToProfileScreen,
            navigateToAuthGraph = navigateToAuthGraph
        )
    }
}

fun NavController.navigateToHomeScreen(navOptions: NavOptions? = null) =
    navigate(BottomNavigationScreen.Home.route, navOptions)