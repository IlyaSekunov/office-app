package ru.ilyasekunov.filters

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.auth.repository.AuthRepository
import ru.ilyasekunov.model.SortingCategory
import ru.ilyasekunov.network.exceptions.HttpForbiddenException
import ru.ilyasekunov.profile.CurrentUserUiState
import javax.inject.Inject

@HiltViewModel
class FiltersViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    var filtersUiState by mutableStateOf(FiltersUiState())
        private set
    var currentUserUiState by mutableStateOf(CurrentUserUiState())
        private set

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            currentUserUiState = currentUserUiState.copy(isLoading = true)

            authRepository.userInfo().also { result ->
                val exception = result.exceptionOrNull()
                currentUserUiState = currentUserUiState.copy(
                    user = result.getOrNull(),
                    isErrorWhileLoading = result.isFailure && exception !is HttpForbiddenException,
                    isUnauthorized = exception is HttpForbiddenException
                )
            }

            currentUserUiState = currentUserUiState.copy(isLoading = false)
        }
    }

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