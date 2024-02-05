package ru.ilyasekunov.officeapp.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.SortingFilter
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import ru.ilyasekunov.officeapp.data.repository.user.UserRepository
import ru.ilyasekunov.officeapp.ui.home.filters.OfficeFilterUiState
import ru.ilyasekunov.officeapp.ui.home.filters.SortingFiltersUiState
import javax.inject.Inject

class FiltersUiState(
    sortingCategoryUiState: SortingFiltersUiState,
    officeFilterUiState: List<OfficeFilterUiState>
) {
    var sortingFiltersUiState by mutableStateOf(sortingCategoryUiState)
        private set
    var officeFiltersUiState by mutableStateOf(officeFilterUiState)
        private set

    fun updateSortingFiltersUiState(sortingFiltersUiState: SortingFiltersUiState) {
        this.sortingFiltersUiState = sortingFiltersUiState
    }

    fun updateOfficeFiltersUiState(officeFiltersUiState: List<OfficeFilterUiState>) {
        this.officeFiltersUiState = officeFiltersUiState
    }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postsRepository: PostsRepository
) : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var filtersUiState: FiltersUiState? = null
        private set

    fun loadFiltersUiState() {
        viewModelScope.launch {
            isLoading = true
            val filters = postsRepository.findFilters()
            val offices = userRepository.findOfficeList()
            filtersUiState = FiltersUiState(
                sortingCategoryUiState = SortingFiltersUiState(
                    filters = filters,
                    selected = filters[0]
                ),
                officeFilterUiState = offices.map { OfficeFilterUiState(it) }
            )
            isLoading = false
        }
    }

    fun updateOfficeFilter(officeFilterUiState: OfficeFilterUiState) {
        filtersUiState?.let { filtersUiState ->
            val newOfficeFilterUiState = filtersUiState.officeFiltersUiState.map {
                if (it == officeFilterUiState) {
                    it.copy(isSelected = !it.isSelected)
                } else {
                    it
                }
            }
            filtersUiState.updateOfficeFiltersUiState(newOfficeFilterUiState)
        }
    }

    fun updateSortingCategory(sortingFilter: SortingFilter) {
        filtersUiState?.let {
            val newSortingCategoryUiState =
                it.sortingFiltersUiState.copy(selected = sortingFilter)
            it.updateSortingFiltersUiState(newSortingCategoryUiState)
        }
    }

    fun discardFilterChanges() {
        filtersUiState?.let {
            val restoredOfficeFilterUiState = it.officeFiltersUiState.map { officeFilter ->
                officeFilter.copy(isSelected = false)
            }
            val sortingCategoryUiState = it.sortingFiltersUiState
            val restoredSortingCategoryUiState =
                sortingCategoryUiState.copy(selected = sortingCategoryUiState.filters[0])
            it.updateOfficeFiltersUiState(restoredOfficeFilterUiState)
            it.updateSortingFiltersUiState(restoredSortingCategoryUiState)
        }
    }
}