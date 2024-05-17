package ru.ilyasekunov.officeapp.ui.filters

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.model.Filters
import ru.ilyasekunov.officeapp.ui.home.OfficeFilterUiState
import ru.ilyasekunov.officeapp.ui.home.SortingFiltersUiState
import ru.ilyasekunov.officeapp.ui.home.toFiltersUiState

@Immutable
data class FiltersUiState(
    val sortingFiltersUiState: SortingFiltersUiState = SortingFiltersUiState(),
    val officeFiltersUiState: List<OfficeFilterUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isErrorWhileLoading: Boolean = false,
    val isLoaded: Boolean = false
)

class FiltersUiStateHolder(
    initialFiltersUiState: FiltersUiState,
    private val coroutineScope: CoroutineScope,
    private val loadFiltersRequest: suspend () -> Result<Filters>
) {
    var filtersUiState by mutableStateOf(initialFiltersUiState)
        private set

    val isLoading get() = filtersUiState.isLoading
    val isLoaded get() = filtersUiState.isLoaded
    val isErrorWhileLoading get() = filtersUiState.isErrorWhileLoading

    fun updateFiltersUiState(filtersUiState: FiltersUiState) {
        this.filtersUiState = filtersUiState
    }

    fun removeSortingFilter() {
        val sortingFiltersUiState = filtersUiState.sortingFiltersUiState
        filtersUiState = filtersUiState.copy(
            sortingFiltersUiState = sortingFiltersUiState.copy(selected = null)
        )
    }

    fun removeOfficeFilter(officeFilter: OfficeFilterUiState) {
        filtersUiState = filtersUiState.copy(
            officeFiltersUiState = filtersUiState.officeFiltersUiState.map {
                if (it == officeFilter) {
                    it.copy(isSelected = false)
                } else {
                    it
                }
            }
        )
    }

    fun loadFilters() {
        coroutineScope.launch {
            filtersUiState = filtersUiState.copy(isLoading = true)
            loadFiltersRequest().also { result ->
                if (result.isSuccess) {
                    val filters = result.getOrThrow()
                    filtersUiState = filters.toFiltersUiState()
                } else {
                    filtersUiState = filtersUiState.copy(
                        isErrorWhileLoading = true,
                        isLoaded = false,
                        isLoading = false
                    )
                }
            }
        }
    }
}