package ru.ilyasekunov.home.navigation

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import ru.ilyasekunov.home.HomeScreen
import ru.ilyasekunov.home.HomeViewModel
import ru.ilyasekunov.navigation.BottomNavigationScreen

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
        val posts = viewModel.postsUiState.data.collectAsLazyPagingItems()
        val lazyListState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        HomeScreen(
            posts = posts,
            currentUserUiState = viewModel.currentUserUiState,
            searchUiState = viewModel.searchUiState,
            onSearchValueChange = viewModel::updateSearchValue,
            filtersUiState = viewModel.filtersUiStateHolder.filtersUiState,
            suggestIdeaToMyOfficeUiState = viewModel.suggestIdeaToMyOfficeUiState,
            deletePostUiState = viewModel.deletePostUiState,
            postsLazyListState = lazyListState,
            onOfficeFilterRemoveClick = viewModel.filtersUiStateHolder::removeOfficeFilter,
            onSortingFilterRemoveClick = viewModel.filtersUiStateHolder::removeSortingFilter,
            onDeletePostClick = viewModel::deletePost,
            onDeletePostResultShown = viewModel::deletePostResultShown,
            onPostLikeClick = viewModel::updateLike,
            onPostDislikeClick = viewModel::updateDislike,
            onRetryInfoLoad = {
                viewModel.loadData()
                posts.retry()
            },
            onPullToRefresh = {
                launch {
                    launch { viewModel.refreshCurrentUser() }
                    posts.refresh()
                }
            },
            onSuggestIdeaToMyOfficeClick = viewModel::suggestIdeaToMyOffice,
            onSuggestIdeaToMyOfficeResultShown = viewModel::suggestIdeaToMyOfficeResultShown,
            navigateToHomeScreen = {
                coroutineScope.launch {
                    lazyListState.scrollToItem(0)
                }
            },
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