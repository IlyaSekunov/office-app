package ru.ilyasekunov.officeapp.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.ui.myideas.MyIdeasScreen
import ru.ilyasekunov.officeapp.ui.myideas.MyIdeasViewModel

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
    composable(route = Screen.MyIdeas.route) {
        val viewModel = hiltViewModel<MyIdeasViewModel>()
        val myIdeas = viewModel.myIdeasUiState.ideas.collectAsLazyPagingItems()
        MyIdeasScreen(
            ideas = myIdeas,
            onPullToRefresh = { launch { myIdeas.refresh() } },
            onDeleteIdeaClick = viewModel::deletePost,
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
    navigate(Screen.MyIdeas.route, navOptions)