package ru.ilyasekunov.officeapp.navigation

import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
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
        val suggestedIdeas = viewModel.suggestedIdeasUiState.data.collectAsLazyPagingItems()
        val ideasInProgress = viewModel.ideasInProgressUiState.data.collectAsLazyPagingItems()
        val implementedIdeas = viewModel.implementedIdeasUiState.data.collectAsLazyPagingItems()
        val officeEmployees = viewModel.officeEmployeesUiState.data.collectAsLazyPagingItems()
        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()
        MyOfficeScreen(
            currentUserUiState = viewModel.currentUserUiState,
            suggestedIdeas = suggestedIdeas,
            ideasInProgress = ideasInProgress,
            implementedIdeas = implementedIdeas,
            officeEmployees = officeEmployees,
            deletePostUiState = viewModel.deletePostUiState,
            scrollState = scrollState,
            onRetryDataLoad = {
                viewModel.loadCurrentUser()
                suggestedIdeas.retry()
                ideasInProgress.retry()
                implementedIdeas.retry()
                officeEmployees.retry()
            },
            onPullToRefresh = {
                launch {
                    launch {
                        viewModel.refreshCurrentUser()
                    }
                    suggestedIdeas.refresh()
                    ideasInProgress.refresh()
                    implementedIdeas.refresh()
                    officeEmployees.refresh()
                }
            },
            onPostLikeClick = viewModel::updateLike,
            onPostDislikeClick = viewModel::updateDislike,
            onPostCommentsClick = {
                navigateToIdeaDetailsScreen(it.id, true)
            },
            onDeletePostClick = viewModel::deletePost,
            onDeletePostResultShown = viewModel::deletePostResultShown,
            onDeletePostSuccess = {
                suggestedIdeas.refresh()
                ideasInProgress.refresh()
                implementedIdeas.refresh()
            },
            navigateToIdeaDetailsScreen = {
                navigateToIdeaDetailsScreen(it, false)
            },
            navigateToEditIdeaScreen = navigateToEditIdeaScreen,
            navigateToAuthorScreen = navigateToAuthorScreen,
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToMyOfficeScreen = {
                coroutineScope.launch {
                    scrollState.scrollTo(0)
                }
            },
            navigateToProfileScreen = navigateToProfileScreen,
            navigateToAuthGraph = navigateToAuthGraph
        )
    }
}

fun NavController.navigateToMyOfficeScreen(navOptions: NavOptions? = null) =
    navigate(BottomNavigationScreen.MyOffice.route, navOptions)