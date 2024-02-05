package ru.ilyasekunov.officeapp.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.ilyasekunov.officeapp.data.SortingFilter
import ru.ilyasekunov.officeapp.data.defaultSortFilter
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.officeList
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import ru.ilyasekunov.officeapp.data.repository.user.UserRepository
import ru.ilyasekunov.officeapp.data.sortingFilters
import javax.inject.Inject

data class FiltersUiState(
    val sortingFiltersUiState: SortingFiltersUiState,
    val officeFiltersUiState: List<OfficeFilterUiState>
) {
    companion object {
        val Default = FiltersUiState(
            sortingFiltersUiState = SortingFiltersUiState(
                filters = sortingFilters,
                selected = defaultSortFilter
            ),
            officeFiltersUiState = officeList.map {
                OfficeFilterUiState(office = it)
            }
        )
    }
}

data class OfficeFilterUiState(
    val office: Office,
    val isSelected: Boolean = false
)

data class SortingFiltersUiState(
    val filters: List<SortingFilter>,
    val selected: SortingFilter
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postsRepository: PostsRepository
) : ViewModel() {
    var filtersUiState by mutableStateOf(FiltersUiState.Default)

    fun updateOfficeFilter(clickedOfficeFilterUiState: OfficeFilterUiState) {
        filtersUiState = filtersUiState.copy(
            officeFiltersUiState = filtersUiState.officeFiltersUiState.map {
                if (it == clickedOfficeFilterUiState) {
                    it.copy(isSelected = !it.isSelected)
                } else {
                    it
                }
            }
        )
    }

    fun updateSortingCategory(clickedSortingFilter: SortingFilter) {
        filtersUiState = filtersUiState.copy(
            sortingFiltersUiState = filtersUiState.sortingFiltersUiState.copy(
                selected = clickedSortingFilter
            )
        )
    }

    fun discardFilterChanges() {
        filtersUiState = FiltersUiState.Default
    }
}