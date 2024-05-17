package ru.ilyasekunov.officeapp.ui.favouriteideas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.dto.SearchPostsDto
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.repository.posts.PostsPagingRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import ru.ilyasekunov.officeapp.ui.PagingDataUiState
import ru.ilyasekunov.officeapp.ui.filters.FiltersUiState
import ru.ilyasekunov.officeapp.ui.filters.FiltersUiStateHolder
import ru.ilyasekunov.officeapp.ui.home.SearchUiState
import ru.ilyasekunov.officeapp.ui.home.toFiltersDto
import ru.ilyasekunov.officeapp.ui.home.toSearchDto
import javax.inject.Inject

@HiltViewModel
class FavouriteIdeasViewModel @Inject constructor(
    postsRepository: PostsRepository,
    private val postsPagingRepository: PostsPagingRepository
) : ViewModel() {
    val favouriteIdeasUiState = PagingDataUiState<IdeaPost>()
    val filtersUiStateHolder = FiltersUiStateHolder(
        initialFiltersUiState = FiltersUiState(),
        coroutineScope = viewModelScope,
        loadFiltersRequest = postsRepository::filters
    )
    var searchUiState by mutableStateOf(SearchUiState())
        private set

    init {
        loadData()
        observeFiltersStateChanges()
    }

    fun loadData() {
        filtersUiStateHolder.loadFilters()
    }

    fun updateSearchValue(searchValue: String) {
        searchUiState = searchUiState.copy(value = searchValue)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeFiltersStateChanges() {
        viewModelScope.launch {
            snapshotFlow {
                if (filtersUiStateHolder.isLoaded) {
                    postsPagingRepository.favouritePosts(
                        searchPostsDto = SearchPostsDto(
                            filtersDto = filtersUiStateHolder.filtersUiState.toFiltersDto(),
                            searchDto = searchUiState.toSearchDto()
                        )
                    )
                        .distinctUntilChanged()
                        .cachedIn(viewModelScope)
                } else {
                    flowOf(PagingData.empty())
                }
            }
                .flatMapLatest { it }
                .collectLatest { favouriteIdeasUiState.updateData(it) }
        }
    }
}