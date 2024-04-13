package ru.ilyasekunov.officeapp.ui.favouriteideas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.dto.SearchPostsDto
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.repository.posts.PostsPagingRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import ru.ilyasekunov.officeapp.ui.home.SearchUiState
import ru.ilyasekunov.officeapp.ui.filters.FiltersUiState
import ru.ilyasekunov.officeapp.ui.filters.FiltersUiStateHolder
import ru.ilyasekunov.officeapp.ui.home.toFiltersDto
import ru.ilyasekunov.officeapp.ui.home.toSearchDto
import javax.inject.Inject

@HiltViewModel
class FavouriteIdeasViewModel @Inject constructor(
    postsRepository: PostsRepository,
    private val postsPagingRepository: PostsPagingRepository
) : ViewModel() {
    private val _favouriteIdeasUiState: MutableStateFlow<PagingData<IdeaPost>> =
        MutableStateFlow(PagingData.empty())
    val favouriteIdeasUiState: StateFlow<PagingData<IdeaPost>> get() = _favouriteIdeasUiState
    val filtersUiStateHolder = FiltersUiStateHolder(
        initialFiltersUiState = FiltersUiState(),
        coroutineScope = viewModelScope,
        loadFiltersRequest = postsRepository::filters,
        onUpdateFiltersState = ::loadFavouritePosts
    )
    var searchUiState by mutableStateOf(SearchUiState())
        private set

    init {
        loadFavouritePosts()
    }

    fun updateSearchValue(searchValue: String) {
        searchUiState = searchUiState.copy(value = searchValue)
        loadFavouritePosts()
    }

    fun loadFavouritePosts() {
        viewModelScope.launch {
            if (!filtersUiStateHolder.isLoaded) {
                filtersUiStateHolder.loadFilters().join()
            }
            if (filtersUiStateHolder.isLoaded) {
                loadFavouritePostsSuspending()
            }
        }
    }

    private suspend fun loadFavouritePostsSuspending() {
        postsPagingRepository.favouritePosts(
            searchPostsDto = SearchPostsDto(
                filtersDto = filtersUiStateHolder.filtersUiState.toFiltersDto(),
                searchDto = searchUiState.toSearchDto()
            )
        )
            .distinctUntilChanged()
            .cachedIn(viewModelScope)
            .collectLatest { _favouriteIdeasUiState.value = it }
    }
}