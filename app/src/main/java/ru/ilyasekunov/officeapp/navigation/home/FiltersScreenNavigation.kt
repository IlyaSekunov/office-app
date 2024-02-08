package ru.ilyasekunov.officeapp.navigation.home

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

fun NavGraphBuilder.filtersScreen(
    viewModelStoreOwnerProvider: () -> ViewModelStoreOwner,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    composable(route = Screen.FiltersScreen.route) { backStackEntry ->
        val viewModelStoreOwner = remember(backStackEntry) { viewModelStoreOwnerProvider() }
        val homeViewModel = hiltViewModel<HomeViewModel>(viewModelStoreOwner)
        FiltersScreen(
            filtersUiState = homeViewModel.filtersUiState,
            applyNewFilters = homeViewModel::updateFiltersUiState,
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