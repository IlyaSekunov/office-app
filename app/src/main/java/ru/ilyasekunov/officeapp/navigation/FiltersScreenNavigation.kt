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
import ru.ilyasekunov.officeapp.ui.home.HomeViewModel
import ru.ilyasekunov.officeapp.ui.home.filters.FiltersScreen
import ru.ilyasekunov.officeapp.ui.home.filters.FiltersUiStateHolder
import ru.ilyasekunov.officeapp.ui.home.filters.FiltersViewModel

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
        val filtersViewModel = hiltViewModel<FiltersViewModel>()
        LaunchedEffect(Unit) {
            filtersViewModel.updateFiltersUiState(filtersUiStateHolder.filtersUiState)
        }
        FiltersScreen(
            filtersUiState = filtersViewModel.filtersUiState,
            onSortingCategoryClick = filtersViewModel::updateSortingCategory,
            onOfficeFilterClick = filtersViewModel::updateOfficeFilterIsSelected,
            onResetClick = filtersViewModel::reset,
            onShowClick = {
                filtersUiStateHolder.updateFiltersUiState(filtersViewModel.filtersUiState)
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
