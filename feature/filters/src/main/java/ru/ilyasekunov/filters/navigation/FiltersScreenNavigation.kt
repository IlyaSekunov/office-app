package ru.ilyasekunov.filters.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.filters.FiltersScreen
import ru.ilyasekunov.filters.FiltersUiState
import ru.ilyasekunov.filters.FiltersUiStateHolder
import ru.ilyasekunov.filters.FiltersViewModel
import ru.ilyasekunov.navigation.Screen

data object FiltersScreen : Screen("filters")

fun NavGraphBuilder.filtersScreen(
    filtersUiStateHolderProvider: () -> FiltersUiStateHolder,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    composable(route = FiltersScreen.route) {
        val filtersUiStateHolder = filtersUiStateHolderProvider()
        val viewModel = setUpFiltersViewModel(filtersUiStateHolder.filtersUiState)

        FiltersScreen(
            filtersUiState = viewModel.filtersUiState,
            currentUserUiState = viewModel.currentUserUiState,
            onSortingCategoryClick = viewModel::updateSortingCategory,
            onOfficeFilterClick = viewModel::updateOfficeFilterIsSelected,
            onResetClick = viewModel::reset,
            onShowClick = {
                filtersUiStateHolder.updateFiltersUiState(viewModel.filtersUiState)
                navigateBack()
            },
            onRetryLoad = {
                filtersUiStateHolder.loadFilters()
                viewModel.loadCurrentUser()
            },
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToMyOfficeScreen = navigateToMyOfficeScreen,
            navigateToProfileScreen = navigateToProfileScreen,
            navigateBack = navigateBack
        )
    }
}

fun NavController.navigateToFiltersScreen(navOptions: NavOptions? = null) =
    navigate(FiltersScreen.route, navOptions)

@Composable
fun setUpFiltersViewModel(filtersUiState: FiltersUiState): FiltersViewModel {
    val viewModel = hiltViewModel<FiltersViewModel>()
    LaunchedEffect(filtersUiState) {
        viewModel.updateFiltersUiState(filtersUiState)
    }
    return viewModel
}