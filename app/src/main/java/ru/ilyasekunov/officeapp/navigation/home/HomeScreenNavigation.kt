package ru.ilyasekunov.officeapp.navigation.home

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.navigation.BottomNavigationScreen
import ru.ilyasekunov.officeapp.ui.home.HomeScreen
import ru.ilyasekunov.officeapp.ui.home.HomeViewModel

fun NavGraphBuilder.homeScreen(
    viewModelStoreOwnerProvider: () -> ViewModelStoreOwner,
    navigateToIdeaDetailsScreen: (postId: Long) -> Unit,
    navigateToSuggestIdeaScreen: () -> Unit,
    navigateToFiltersScreen: () -> Unit,
    navigateToAuthorScreen: (authorId: Long) -> Unit,
    navigateToEditIdeaScreen: (postId: Long) -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit
) {
    composable(route = BottomNavigationScreen.Home.route) {
        val homeViewModel = hiltViewModel<HomeViewModel>(viewModelStoreOwnerProvider())
        HomeScreen(
            postsUiState = homeViewModel.postsUiState,
            isIdeaAuthorCurrentUser = homeViewModel::isIdeaAuthorCurrentUser,
            searchUiState = homeViewModel.searchUiState,
            onSearchValueChange = homeViewModel::updateSearchValue,
            filtersUiState = homeViewModel.filtersUiState,
            onOfficeFilterRemoveClick = homeViewModel::removeOfficeFilter,
            onSortingFilterRemoveClick = homeViewModel::removeSortingFilter,
            onDeletePostClick = homeViewModel::deletePost,
            onPostLikeClick = homeViewModel::updateLike,
            onPostDislikeClick = homeViewModel::updateDislike,
            onCommentClick = { /*TODO*/ },
            navigateToFiltersScreen = navigateToFiltersScreen,
            navigateToSuggestIdeaScreen = navigateToSuggestIdeaScreen,
            navigateToIdeaDetailsScreen = navigateToIdeaDetailsScreen,
            navigateToAuthorScreen = navigateToAuthorScreen,
            navigateToEditIdeaScreen = navigateToEditIdeaScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToMyOfficeScreen = navigateToMyOfficeScreen,
            navigateToProfileScreen = navigateToProfileScreen
        )
    }
}

fun NavController.navigateToHomeScreen(navOptions: NavOptions? = null) =
    navigate(BottomNavigationScreen.Home.route, navOptions)