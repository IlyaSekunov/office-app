package ru.ilyasekunov.officeapp.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.mapSaver
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
        val Saver = object : Saver<FiltersUiState, Map<String, Any>> {
            override fun restore(value: Map<String, Any>): FiltersUiState {
                val savedSortingFiltersStateMap = (value["saved_sorting_filters_ui_state"] as Map<*, *>)
                val savedOfficeFiltersStateMap = (value["saved_office_filters_ui_state"] as List<*>)
                val sortingFiltersUiState = SortingFiltersUiState(
                    selected = SortingFilter(
                        id = (savedSortingFiltersStateMap["sorting_selected_id"] as Int),
                        name = (savedSortingFiltersStateMap["sorting_selected_name"] as String)
                    ),
                    filters = (savedSortingFiltersStateMap["sorting_filters_list"] as List<*>).map {
                        it as Map<*, *>
                        SortingFilter(
                            id = (it["sorting_selected_id"] as Int),
                            name = (it["sorting_selected_name"] as String)
                        )
                    }
                )
                val officeFiltersUiState = savedOfficeFiltersStateMap.map {
                    it as Map<*, *>
                    OfficeFilterUiState(
                        isSelected = (it["is_selected"] as Boolean),
                        office = Office(
                            id = (it["office_id"] as Int),
                            imageUrl = (it["office_imageUrl"] as String),
                            address = (it["office_address"] as String)
                        )
                    )
                }
                return FiltersUiState(
                    sortingFiltersUiState = sortingFiltersUiState,
                    officeFiltersUiState = officeFiltersUiState
                )
            }

            override fun SaverScope.save(value: FiltersUiState): Map<String, Any> {
                val savedFilterUiStateList = value.officeFiltersUiState.map {
                    mapOf<String, Any>(
                        "is_selected" to it.isSelected,
                        "office_id" to it.office.id,
                        "office_imageUrl" to it.office.imageUrl,
                        "office_address" to it.office.address
                    )
                }
                val sortingFilterUiStateMap = mutableMapOf<String, Any>(
                    "sorting_selected_id" to value.sortingFiltersUiState.selected.id,
                    "sorting_selected_name" to value.sortingFiltersUiState.selected.name
                )
                sortingFilterUiStateMap +=
                    "sorting_filters_list" to value.sortingFiltersUiState.filters.map {
                        mapOf<String, Any>(
                            "sorting_selected_id" to it.id,
                            "sorting_selected_name" to it.name
                        )
                    }
                return mapOf(
                    "saved_office_filters_ui_state" to savedFilterUiStateList,
                    "saved_sorting_filters_ui_state" to sortingFilterUiStateMap
                )
            }
        }
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
    private val postsRepository: PostsRepository,
    private val userRepository: UserRepository
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
            val userId = userRepository.findUser()!!.id
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
            if (isPressed) {
                postsRepository.pressLike(changedPost.id, userId)
            } else {
                postsRepository.removeLike(changedPost.id, userId)
            }
        }
    }

    fun updateDislike(post: IdeaPost, isPressed: Boolean) {
        viewModelScope.launch {
            val dislikesCount = if (isPressed) post.dislikesCount + 1 else post.dislikesCount - 1
            val userId = userRepository.findUser()!!.id
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
            if (isPressed) {
                postsRepository.pressDislike(changedPost.id, userId)
            } else {
                postsRepository.removeDislike(changedPost.id, userId)
            }
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