package ru.ilyasekunov.officeapp.ui.filters

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.ilyasekunov.officeapp.data.model.SortingCategory
import ru.ilyasekunov.officeapp.ui.home.OfficeFilterUiState
import javax.inject.Inject

@HiltViewModel
class FiltersViewModel @Inject constructor(): ViewModel() {
    var filtersUiState by mutableStateOf(FiltersUiState())
        private set

    fun updateFiltersUiState(filtersUiState: FiltersUiState) {
        this.filtersUiState = filtersUiState
    }

    fun updateOfficeFilterIsSelected(officeFilterUiState: OfficeFilterUiState) {
        filtersUiState = filtersUiState.copy(
            officeFiltersUiState = filtersUiState.officeFiltersUiState.map {
                if (it == officeFilterUiState) {
                    it.copy(isSelected = !it.isSelected)
                } else {
                    it
                }
            }
        )
    }

    fun updateSortingCategory(sortingCategory: SortingCategory) {
        filtersUiState = filtersUiState.copy(
            sortingFiltersUiState = filtersUiState.sortingFiltersUiState.copy(
                selected = sortingCategory
            )
        )
    }

    fun reset() {
        filtersUiState = filtersUiState.copy(
            sortingFiltersUiState = filtersUiState.sortingFiltersUiState.copy(selected = null),
            officeFiltersUiState = filtersUiState.officeFiltersUiState.map {
                it.copy(isSelected = false)
            }
        )
    }
}