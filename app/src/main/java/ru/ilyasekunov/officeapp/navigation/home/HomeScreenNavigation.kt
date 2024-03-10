package ru.ilyasekunov.officeapp.navigation.home

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import ru.ilyasekunov.officeapp.navigation.BottomNavigationScreen
import ru.ilyasekunov.officeapp.ui.home.HomeScreen
import ru.ilyasekunov.officeapp.ui.home.HomeViewModel
import ru.ilyasekunov.officeapp.ui.home.PostsUiState

fun NavGraphBuilder.homeScreen(
    viewModelStoreOwnerProvider: () -> ViewModelStoreOwner,
    navigateToIdeaDetailsScreen: (postId: Long) -> Unit,
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
        val homeViewModel = hiltViewModel<HomeViewModel>(viewModelStoreOwnerProvider())
        val lazyPagingPosts = homeViewModel.postsUiState.collectAsLazyPagingItems()
        val postsUiState = remember(lazyPagingPosts.itemSnapshotList, lazyPagingPosts.loadState) {
            PostsUiState(
                posts = lazyPagingPosts.itemSnapshotList.items,
                isRefreshing = lazyPagingPosts.loadState.refresh == LoadState.Loading,
                isAppending = lazyPagingPosts.loadState.append == LoadState.Loading,
                isErrorWhileLoading = lazyPagingPosts.loadState.hasError
            )
        }
        HomeScreen(
            postsUiState = postsUiState,
            currentUserUiState = homeViewModel.currentUserUiState,
            searchUiState = homeViewModel.searchUiState,
            onSearchValueChange = homeViewModel::updateSearchValue,
            filtersUiState = homeViewModel.filtersUiState,
            onOfficeFilterRemoveClick = homeViewModel::removeOfficeFilter,
            onSortingFilterRemoveClick = homeViewModel::removeSortingFilter,
            onDeletePostClick = homeViewModel::deletePost,
            onPostLikeClick = homeViewModel::updateLike,
            onPostDislikeClick = homeViewModel::updateDislike,
            onCommentClick = { /*TODO*/ },
            onRetryPostsLoad = homeViewModel::loadPosts,
            onPullToRefresh = lazyPagingPosts::refresh,
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