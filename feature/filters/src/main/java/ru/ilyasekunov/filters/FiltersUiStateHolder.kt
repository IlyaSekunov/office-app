package ru.ilyasekunov.filters

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.ilyasekunov.dto.FiltersDto
import ru.ilyasekunov.model.Filters
import ru.ilyasekunov.model.Office
import ru.ilyasekunov.model.SortingCategory

@Immutable
data class OfficeFilterUiState(
    val office: Office,
    val isSelected: Boolean = false
)

@Immutable
data class SortingFiltersUiState(
    val filters: List<SortingCategory> = emptyList(),
    val selected: SortingCategory? = null
)

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

    val isLoaded get() = filtersUiState.isLoaded

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

fun Filters.toFiltersUiState(): FiltersUiState =
    FiltersUiState(
        sortingFiltersUiState = SortingFiltersUiState(
            filters = sortingCategories
        ),
        officeFiltersUiState = offices.map {
            OfficeFilterUiState(office = it)
        },
        isLoaded = true
    )

fun FiltersUiState.toFiltersDto(): FiltersDto {
    var offices = officeFiltersUiState
        .asSequence()
        .filter { it.isSelected }
        .map { it.office.id }
        .toList()
    if (offices.isEmpty()) {
        offices = officeFiltersUiState.map { it.office.id }
    }
    return FiltersDto(
        offices = offices,
        sortingFilter = sortingFiltersUiState.selected?.id
    )
}