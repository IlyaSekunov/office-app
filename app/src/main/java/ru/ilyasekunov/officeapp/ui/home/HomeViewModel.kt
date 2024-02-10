package ru.ilyasekunov.officeapp.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
        observePosts()
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
        viewModelScope.launch {
            val likesCount = if (isPressed) post.likesCount + 1 else post.likesCount - 1
            val changedPost =
                if (post.isDislikePressed) {
                    post.copy(
                        isDislikePressed = false,
                        dislikesCount = post.dislikesCount - 1,
                        isLikePressed = isPressed,
                        likesCount = likesCount
                    )
                } else {
                    post.copy(
                        isLikePressed = isPressed,
                        likesCount = likesCount
                    )
                }
            posts = posts.map {
                if (it.id == post.id) {
                    changedPost
                } else {
                    it
                }
            }
            postsRepository.updatePost(changedPost)
        }
    }

    fun updateDislike(post: IdeaPost, isPressed: Boolean) {
        viewModelScope.launch {
            val dislikesCount = if (isPressed) post.dislikesCount + 1 else post.dislikesCount - 1
            val changedPost = if (post.isLikePressed) {
                post.copy(
                    isLikePressed = false,
                    likesCount = post.likesCount - 1,
                    isDislikePressed = isPressed,
                    dislikesCount = dislikesCount
                )
            } else {
                post.copy(
                    isDislikePressed = isPressed,
                    dislikesCount = dislikesCount
                )
            }
            posts = posts.map {
                if (it.id == post.id) {
                    changedPost
                } else {
                    it
                }
            }
            postsRepository.updatePost(changedPost)
        }
    }

    fun deletePost(post: IdeaPost) {
        viewModelScope.launch {
            posts -= post
            postsRepository.deletePostById(post.id)
        }
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            posts = postsRepository.findPosts()
        }
    }

    private fun observePosts() {
        viewModelScope.launch {
            while (true) {
                val fetchedPosts = postsRepository.findPosts()
                if (fetchedPosts != posts) {
                    posts = fetchedPosts
                }
                delay(5000)
            }
        }
    }
}