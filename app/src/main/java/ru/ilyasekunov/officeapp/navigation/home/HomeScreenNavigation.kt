package ru.ilyasekunov.officeapp.navigation.home

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.navigation.BottomNavigationScreen
import ru.ilyasekunov.officeapp.ui.home.HomeScreen
import ru.ilyasekunov.officeapp.ui.home.HomeViewModel
import ru.ilyasekunov.officeapp.ui.userprofile.UserViewModel

fun NavGraphBuilder.homeScreen(
    homeViewModelStoreOwnerProvider: () -> ViewModelStoreOwner,
    userViewModelStoreOwnerProvider: () -> ViewModelStoreOwner,
    navigateToIdeaDetailsScreen: (postId: Long) -> Unit,
    navigateToSuggestIdeaScreen: () -> Unit,
    navigateToFiltersScreen: () -> Unit,
    navigateToAuthorScreen: (authorId: Long) -> Unit,
    navigateToEditIdeaScreen: (postId: Long) -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit
) {
    composable(route = BottomNavigationScreen.Home.route) { backStackEntry ->
        val homeViewModelStoreOwner = remember(backStackEntry) { homeViewModelStoreOwnerProvider() }
        val userViewModelStoreOwner = remember(backStackEntry) { userViewModelStoreOwnerProvider() }
        val homeViewModel = hiltViewModel<HomeViewModel>(homeViewModelStoreOwner)
        val userViewModel = hiltViewModel<UserViewModel>(userViewModelStoreOwner)
        HomeScreen(
            posts = homeViewModel.posts,
            isIdeaAuthorCurrentUser = {
                it.id == userViewModel.user!!.id
            },
            searchValue = homeViewModel.searchUiState,
            onSearchValueChange = homeViewModel::updateSearch,
            filtersUiState = homeViewModel.filtersUiState,
            onOfficeFilterRemoveClick = homeViewModel::removeOfficeFilter,
            onSortingFilterRemoveClick = homeViewModel::removeSortingFilter,
            onDeletePostClick = {},
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