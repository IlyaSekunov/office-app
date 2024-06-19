package ru.ilyasekunov.myideas.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import ru.ilyasekunov.myideas.MyIdeasScreen
import ru.ilyasekunov.myideas.MyIdeasViewModel
import ru.ilyasekunov.navigation.Screen

data object MyIdeasScreen : Screen("my-ideas")

fun NavGraphBuilder.myIdeasScreen(
    navigateToIdeaDetailsScreen: (Long) -> Unit,
    navigateToSuggestIdeaScreen: () -> Unit,
    navigateToEditIdeaScreen: (Long) -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    composable(route = MyIdeasScreen.route) {
        val viewModel = hiltViewModel<MyIdeasViewModel>()
        val myIdeas = viewModel.myIdeasUiState.data.collectAsLazyPagingItems()

        MyIdeasScreen(
            ideas = myIdeas,
            deletePostUiState = viewModel.deleteIdeaUiState,
            onPullToRefresh = { launch { myIdeas.refresh() } },
            onDeleteIdeaClick = viewModel::deletePost,
            onDeleteResultShown = viewModel::deletePostResultShown,
            navigateToIdeaDetailsScreen = navigateToIdeaDetailsScreen,
            navigateToSuggestIdeaScreen = navigateToSuggestIdeaScreen,
            navigateToEditIdeaScreen = navigateToEditIdeaScreen,
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToProfileScreen = navigateToProfileScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToMyOfficeScreen = navigateToMyOfficeScreen,
            navigateBack = navigateBack
        )
    }
}

fun NavController.navigateToMyIdeasScreen(navOptions: NavOptions? = null) =
    navigate(MyIdeasScreen.route, navOptions)