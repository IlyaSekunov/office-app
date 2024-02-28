package ru.ilyasekunov.officeapp.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.model.Filters
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.SortingCategory
import ru.ilyasekunov.officeapp.data.model.User
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import ru.ilyasekunov.officeapp.data.repository.user.UserRepository
import javax.inject.Inject

data class FiltersUiState(
    val sortingFiltersUiState: SortingFiltersUiState = SortingFiltersUiState(),
    val officeFiltersUiState: List<OfficeFilterUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isErrorWhileLoading: Boolean = false
)

data class OfficeFilterUiState(
    val office: Office,
    val isSelected: Boolean = false
)

data class SortingFiltersUiState(
    val filters: List<SortingCategory> = emptyList(),
    val selected: SortingCategory? = null
)

data class PostsUiState(
    val posts: List<IdeaPost> = emptyList(),
    val isLoading: Boolean = false,
    val isErrorWhileLoading: Boolean = false
)

data class CurrentUserUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isErrorWhileLoading: Boolean = false
)

data class SearchUiState(
    val value: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    var postsUiState by mutableStateOf(PostsUiState())
        private set
    var filtersUiState by mutableStateOf(FiltersUiState())
        private set
    var searchUiState by mutableStateOf(SearchUiState())
        private set
    private var currentUserUiState by mutableStateOf(CurrentUserUiState())

    init {
        observePosts()
        loadPosts()
        loadFilters()
        loadCurrentUser()
    }

    fun updateFiltersUiState(filtersUiState: FiltersUiState) {
        this.filtersUiState = filtersUiState
    }

    fun updateSearchValue(searchValue: String) {
        searchUiState = searchUiState.copy(value = searchValue)
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
        val sortingFiltersUiState = filtersUiState.sortingFiltersUiState
        filtersUiState = filtersUiState.copy(
            sortingFiltersUiState = sortingFiltersUiState.copy(selected = null)
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
            updatePost(changedPost)
            if (isPressed) {
                postsRepository.pressLike(changedPost.id)
            } else {
                postsRepository.removeLike(changedPost.id)
            }
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
            updatePost(changedPost)
            if (isPressed) {
                postsRepository.pressDislike(changedPost.id)
            } else {
                postsRepository.removeDislike(changedPost.id)
            }
        }
    }

    fun deletePost(post: IdeaPost) {
        viewModelScope.launch {
            updateIsPostsLoading(true)
            postsUiState = postsUiState.copy(
                posts = postsUiState.posts - post
            )
            postsRepository.deletePostById(post.id)
            updateIsPostsLoading(false)
        }
    }

    fun isIdeaAuthorCurrentUser(ideaAuthor: IdeaAuthor) =
        ideaAuthor.id == currentUserUiState.user!!.id

    private fun updateIsPostsLoading(isLoading: Boolean) {
        postsUiState = postsUiState.copy(isLoading = isLoading)
    }

    private fun updatePost(updatedPost: IdeaPost) {
        postsUiState = postsUiState.copy(
            posts = postsUiState.posts.map {
                if (it.id == updatedPost.id) {
                    updatedPost
                } else {
                    it
                }
            }
        )
    }

    private fun updatePosts(posts: List<IdeaPost>) {
        postsUiState = postsUiState.copy(posts = posts)
    }

    private fun updateIsCurrentUserLoading(isLoading: Boolean) {
        currentUserUiState = currentUserUiState.copy(isLoading = isLoading)
    }

    private fun updateIsFiltersLoading(isLoading: Boolean) {
        filtersUiState = filtersUiState.copy(isLoading = isLoading)
    }

    private fun updateIsErrorWhileUserLoading(isErrorWhileLoading: Boolean) {
        currentUserUiState = currentUserUiState.copy(
            isErrorWhileLoading = isErrorWhileLoading
        )
    }

    private fun updateIsErrorWhileFiltersLoading(isErrorWhileLoading: Boolean) {
        filtersUiState = filtersUiState.copy(isErrorWhileLoading = isErrorWhileLoading)
    }

    private fun updateIsErrorWhilePostsLoading(isErrorWhileLoading: Boolean) {
        postsUiState = postsUiState.copy(isErrorWhileLoading = isErrorWhileLoading)
    }

    private fun updateUser(user: User?) {
        currentUserUiState = currentUserUiState.copy(user = user)
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            updateIsCurrentUserLoading(true)
            val userResult = userRepository.user()
            if (userResult.isSuccess) {
                val user = userResult.getOrThrow()
                updateIsErrorWhileUserLoading(false)
                updateUser(user)
            } else {
                updateIsErrorWhileUserLoading(true)
            }
            updateIsCurrentUserLoading(false)
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            updateIsPostsLoading(true)
            val postsResult = postsRepository.posts()
            if (postsResult.isSuccess) {
                val posts = postsResult.getOrThrow()
                updatePosts(posts)
                updateIsErrorWhilePostsLoading(false)
            } else {
                updateIsErrorWhilePostsLoading(true)
            }
            updateIsPostsLoading(false)
        }
    }

    private fun loadFilters() {
        viewModelScope.launch {
            updateIsFiltersLoading(true)
            val filtersResult = postsRepository.filters()
            if (filtersResult.isSuccess) {
                val filters = filtersResult.getOrThrow()
                filtersUiState = filters.toFiltersUiState()
                updateIsErrorWhileFiltersLoading(false)
            } else {
                updateIsErrorWhileFiltersLoading(true)
            }
            updateIsFiltersLoading(false)
        }
    }

    private fun observePosts() {
        viewModelScope.launch {
            while (true) {
                val postsResult = postsRepository.posts()
                if (postsResult.isSuccess) {
                    val posts = postsResult.getOrThrow()
                    if (posts != postsUiState.posts) {
                        updatePosts(posts)
                    }
                    updateIsErrorWhilePostsLoading(false)
                } else {
                    updateIsErrorWhilePostsLoading(true)
                }
                delay(5000)
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
        }
    )