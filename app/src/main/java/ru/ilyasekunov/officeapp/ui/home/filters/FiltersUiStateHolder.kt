package ru.ilyasekunov.officeapp.ui.home.filters

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.model.Filters
import ru.ilyasekunov.officeapp.ui.home.OfficeFilterUiState
import ru.ilyasekunov.officeapp.ui.home.SortingFiltersUiState
import ru.ilyasekunov.officeapp.ui.home.toFiltersUiState

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
    private val loadFiltersRequest: suspend () -> Result<Filters>,
    private val onUpdateFiltersState: (() -> Unit)? = null
) {
    var filtersUiState by mutableStateOf(initialFiltersUiState)
        private set

    val isLoading get() = filtersUiState.isLoading
    val isLoaded get() = filtersUiState.isLoaded
    val isErrorWhileLoading get() = filtersUiState.isErrorWhileLoading

    fun updateFiltersUiState(filtersUiState: FiltersUiState) {
        this.filtersUiState = filtersUiState
        onUpdateFiltersState?.invoke()
    }

    fun removeSortingFilter() {
        val sortingFiltersUiState = filtersUiState.sortingFiltersUiState
        filtersUiState = filtersUiState.copy(
            sortingFiltersUiState = sortingFiltersUiState.copy(selected = null)
        )
        onUpdateFiltersState?.invoke()
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
        onUpdateFiltersState?.invoke()
    }

    private fun updateIsFiltersLoading(isLoading: Boolean) {
        filtersUiState = filtersUiState.copy(isLoading = isLoading)
    }

    private fun updateIsErrorWhileFiltersLoading(isErrorWhileLoading: Boolean) {
        filtersUiState = filtersUiState.copy(isErrorWhileLoading = isErrorWhileLoading)
    }

    private fun updateFiltersIsLoaded(isLoaded: Boolean) {
        filtersUiState = filtersUiState.copy(isLoaded = isLoaded)
    }

    fun loadFilters(): Job =
        coroutineScope.launch {
            updateIsFiltersLoading(true)
            val filtersResult = loadFiltersRequest()
            if (filtersResult.isSuccess) {
                val filters = filtersResult.getOrThrow()
                filtersUiState = filters.toFiltersUiState()
                updateIsErrorWhileFiltersLoading(false)
                updateFiltersIsLoaded(true)
            } else {
                updateIsErrorWhileFiltersLoading(true)
                updateFiltersIsLoaded(false)
            }
            updateIsFiltersLoading(false)
        }
}