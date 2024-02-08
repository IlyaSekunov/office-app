package ru.ilyasekunov.officeapp.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.SortingFilter
import ru.ilyasekunov.officeapp.data.defaultSortFilter
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.officeList
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
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
    private val postsRepository: PostsRepository
) : ViewModel() {
    var posts: List<IdeaPost> by mutableStateOf(emptyList())
        private set
    var filtersUiState by mutableStateOf(FiltersUiState.Default)
        private set
    var searchUiState by mutableStateOf("")
        private set

    init {
        fetchPosts()
    }

    fun updateFiltersUiState(filtersUiState: FiltersUiState) {
        this.filtersUiState = filtersUiState
    }

    fun updateSearch(searchValue: String) {
        searchUiState = searchValue
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

    fun removeSortingFilter() {
        filtersUiState = filtersUiState.copy(
            sortingFiltersUiState = filtersUiState.sortingFiltersUiState.copy(
                selected = defaultSortFilter
            )
        )
    }

    fun updateLike(post: IdeaPost, isPressed: Boolean) {
        posts = posts.map {
            if (it.id == post.id) {
                if (it.isDislikePressed) {
                    it.copy(
                        isDislikePressed = false,
                        dislikesCount = it.dislikesCount - 1,
                        isLikePressed = isPressed,
                        likesCount = if (isPressed) it.likesCount + 1 else it.likesCount - 1
                    )
                } else {
                    it.copy(
                        isLikePressed = isPressed,
                        likesCount = if (isPressed) it.likesCount + 1 else it.likesCount - 1
                    )
                }
            } else {
                it
            }
        }
    }

    fun updateDislike(post: IdeaPost, isPressed: Boolean) {
        posts = posts.map {
            if (it.id == post.id) {
                if (it.isLikePressed) {
                    it.copy(
                        isLikePressed = false,
                        likesCount = it.likesCount - 1,
                        isDislikePressed = isPressed,
                        dislikesCount = if (isPressed) it.dislikesCount + 1 else it.dislikesCount - 1
                    )
                } else {
                    it.copy(
                        isDislikePressed = isPressed,
                        dislikesCount = if (isPressed) it.dislikesCount + 1 else it.dislikesCount - 1
                    )
                }
            } else {
                it
            }
        }
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            posts = postsRepository.findPosts()
        }
    }
}