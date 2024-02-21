package ru.ilyasekunov.officeapp.navigation.home

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.navigation.Screen
import ru.ilyasekunov.officeapp.ui.home.HomeViewModel
import ru.ilyasekunov.officeapp.ui.home.filters.FiltersScreen
import ru.ilyasekunov.officeapp.ui.home.filters.FiltersViewModel

fun NavGraphBuilder.filtersScreen(
    homeViewModelStoreOwnerProvider: () -> ViewModelStoreOwner,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    composable(route = Screen.FiltersScreen.route) { backStackEntry ->
        val viewModelStoreOwner = remember(backStackEntry) { homeViewModelStoreOwnerProvider() }
        val homeViewModel = hiltViewModel<HomeViewModel>(viewModelStoreOwner)
        val filtersViewModel = hiltViewModel<FiltersViewModel>()
        LaunchedEffect(homeViewModel.filtersUiState) {
            filtersViewModel.updateFiltersUiState(homeViewModel.filtersUiState)
        }

        FiltersScreen(
            filtersUiState = filtersViewModel.filtersUiState,
            onSortingCategoryClick = filtersViewModel::updateSortingCategory,
            onOfficeFilterClick = filtersViewModel::updateOfficeFilterIsSelected,
            onResetClick = filtersViewModel::reset,
            onShowClick = {
                homeViewModel.updateFiltersUiState(filtersViewModel.filtersUiState)
                navigateToHomeScreen()
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
    navigate(Screen.FiltersScreen.route, navOptions)