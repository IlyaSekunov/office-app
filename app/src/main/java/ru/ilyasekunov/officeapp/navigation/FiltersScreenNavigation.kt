package ru.ilyasekunov.officeapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.ui.favouriteideas.FavouriteIdeasViewModel
import ru.ilyasekunov.officeapp.ui.filters.FiltersScreen
import ru.ilyasekunov.officeapp.ui.filters.FiltersUiState
import ru.ilyasekunov.officeapp.ui.filters.FiltersUiStateHolder
import ru.ilyasekunov.officeapp.ui.filters.FiltersViewModel
import ru.ilyasekunov.officeapp.ui.home.HomeViewModel

fun NavGraphBuilder.filtersScreen(
    previousBackStackEntryProvider: () -> NavBackStackEntry,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    composable(route = Screen.FiltersScreen.route) {
        val previousBackStackEntry = remember { previousBackStackEntryProvider() }
        val filtersUiStateHolder = filtersUiStateHolder(previousBackStackEntry)
        val viewModel = setUpFiltersViewModel(filtersUiStateHolder.filtersUiState)
        FiltersScreen(
            filtersUiState = viewModel.filtersUiState,
            onSortingCategoryClick = viewModel::updateSortingCategory,
            onOfficeFilterClick = viewModel::updateOfficeFilterIsSelected,
            onResetClick = viewModel::reset,
            onShowClick = {
                filtersUiStateHolder.updateFiltersUiState(viewModel.filtersUiState)
                navigateBack()
            },
            onRetryLoad = filtersUiStateHolder::loadFilters,
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToMyOfficeScreen = navigateToMyOfficeScreen,
            navigateToProfileScreen = navigateToProfileScreen,
            navigateBack = navigateBack
        )
    }
}

fun NavController.navigateToFiltersScreen(navOptions: NavOptions? = null) =
    navigate(Screen.FiltersScreen.route, navOptions)

@Composable
private fun filtersUiStateHolder(previousBackStackEntry: NavBackStackEntry): FiltersUiStateHolder =
    when (previousBackStackEntry.destination.route) {
        BottomNavigationScreen.Home.route -> {
            hiltViewModel<HomeViewModel>(previousBackStackEntry).filtersUiStateHolder
        }

        BottomNavigationScreen.Favourite.route -> {
            hiltViewModel<FavouriteIdeasViewModel>(previousBackStackEntry).filtersUiStateHolder
        }

        else -> throw IllegalStateException("No filters ui state holders associated with $previousBackStackEntry")
    }

@Composable
fun setUpFiltersViewModel(filtersUiState: FiltersUiState): FiltersViewModel {
    val viewModel = hiltViewModel<FiltersViewModel>()
    LaunchedEffect(filtersUiState) {
        viewModel.updateFiltersUiState(filtersUiState)
    }
    return viewModel
}