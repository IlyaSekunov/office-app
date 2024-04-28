package ru.ilyasekunov.officeapp.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import ru.ilyasekunov.officeapp.ui.office.MyOfficeScreen
import ru.ilyasekunov.officeapp.ui.office.MyOfficeViewModel

fun NavGraphBuilder.myOfficeScreen(
    navigateToIdeaDetailsScreen: (postId: Long, initiallyScrollToComments: Boolean) -> Unit,
    navigateToAuthorScreen: (authorId: Long) -> Unit,
    navigateToEditIdeaScreen: (postId: Long) -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateToAuthGraph: () -> Unit
) {
    composable(route = BottomNavigationScreen.MyOffice.route) {
        val viewModel = hiltViewModel<MyOfficeViewModel>()
        val suggestedIdeas = viewModel.suggestedIdeasUiState.ideas.collectAsLazyPagingItems()
        val ideasInProgress = viewModel.ideasInProgressUiState.ideas.collectAsLazyPagingItems()
        val implementedIdeas = viewModel.implementedIdeasUiState.ideas.collectAsLazyPagingItems()
        val officeEmployees = viewModel.officeEmployeesUiState.employees.collectAsLazyPagingItems()
        MyOfficeScreen(
            currentUserUiState = viewModel.currentUserUiState,
            suggestedIdeas = suggestedIdeas,
            ideasInProgress = ideasInProgress,
            implementedIdeas = implementedIdeas,
            officeEmployees = officeEmployees,
            onRetryDataLoad = viewModel::loadData,
            onPullToRefresh = {
                suggestedIdeas.refresh()
                ideasInProgress.refresh()
                implementedIdeas.refresh()
                officeEmployees.refresh()
            },
            onPostLikeClick = viewModel::updateLike,
            onPostDislikeClick = viewModel::updateDislike,
            onPostCommentsClick = {
                navigateToIdeaDetailsScreen(it.id, true)
            },
            onDeletePostClick = viewModel::deletePost,
            navigateToIdeaDetailsScreen = {
                navigateToIdeaDetailsScreen(it, false)
            },
            navigateToEditIdeaScreen = navigateToEditIdeaScreen,
            navigateToAuthorScreen = navigateToAuthorScreen,
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToProfileScreen = navigateToProfileScreen,
            navigateToAuthGraph = navigateToAuthGraph
        )
    }
}

fun NavController.navigateToMyOfficeScreen(navOptions: NavOptions? = null) =
    navigate(BottomNavigationScreen.MyOffice.route, navOptions)