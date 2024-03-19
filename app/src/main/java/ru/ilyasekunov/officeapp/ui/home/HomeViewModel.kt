package ru.ilyasekunov.officeapp.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.dto.FiltersDto
import ru.ilyasekunov.officeapp.data.dto.SearchDto
import ru.ilyasekunov.officeapp.data.model.Filters
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.SortingCategory
import ru.ilyasekunov.officeapp.data.model.User
import ru.ilyasekunov.officeapp.data.repository.auth.AuthRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsPagingRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import ru.ilyasekunov.officeapp.exceptions.HttpForbiddenException
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

data class CurrentUserUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isErrorWhileLoading: Boolean = false,
    val isUnauthorized: Boolean = false
)

data class SearchUiState(
    val value: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val postsPagingRepository: PostsPagingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _postsUiState: MutableStateFlow<PagingData<IdeaPost>> =
        MutableStateFlow(PagingData.empty())
    val postsUiState: StateFlow<PagingData<IdeaPost>> get() = _postsUiState
    var filtersUiState by mutableStateOf(FiltersUiState())
        private set
    var searchUiState by mutableStateOf(SearchUiState())
        private set
    var currentUserUiState by mutableStateOf(CurrentUserUiState())
        private set

    init {
        loadCurrentUser()
        loadFilters()
        loadPosts()
    }

    fun updateFiltersUiState(filtersUiState: FiltersUiState) {
        this.filtersUiState = filtersUiState
        loadPosts()
    }

    fun updateSearchValue(searchValue: String) {
        searchUiState = searchUiState.copy(value = searchValue)
        loadPosts()
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
        loadPosts()
    }

    fun removeSortingFilter() {
        val sortingFiltersUiState = filtersUiState.sortingFiltersUiState
        filtersUiState = filtersUiState.copy(
            sortingFiltersUiState = sortingFiltersUiState.copy(selected = null)
        )
        loadPosts()
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
            val deletePostResult = postsRepository.deletePostById(post.id)
            if (deletePostResult.isSuccess) {
                removePost(post)
            }
        }
    }

    fun loadPosts() {
        viewModelScope.launch {
            loadPostSuspending()
        }
    }

    private fun updatePostsPagingData(postPagingData: PagingData<IdeaPost>) {
        _postsUiState.value = postPagingData
    }

    private fun updatePost(updatedPost: IdeaPost) {
        _postsUiState.update { pagingData ->
            pagingData.map { if (it.id == updatedPost.id) updatedPost else it }
        }
    }

    private fun removePost(post: IdeaPost) {
        _postsUiState.update { pagingData ->
            pagingData.filter { it.id != post.id }
        }
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

    private fun updateIsUserUnauthorized(isUnauthorized: Boolean) {
        currentUserUiState = currentUserUiState.copy(isUnauthorized = isUnauthorized)
    }

    private fun updateUser(user: User?) {
        currentUserUiState = currentUserUiState.copy(user = user)
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            updateIsCurrentUserLoading(true)
            val userInfoResult = authRepository.userInfo()
            when {
                userInfoResult.isSuccess -> {
                    val user = userInfoResult.getOrThrow()
                    updateIsErrorWhileUserLoading(false)
                    updateUser(user)
                }

                userInfoResult.exceptionOrNull()!! is HttpForbiddenException -> {
                    updateIsUserUnauthorized(true)
                }

                else -> {
                    updateIsErrorWhileUserLoading(true)
                }
            }
            updateIsCurrentUserLoading(false)
        }
    }

    private suspend fun loadPostSuspending() {
        postsPagingRepository.posts(
            filtersDto = filtersUiState.toFiltersDto(),
            searchDto = searchUiState.toSearchDto()
        )
            .distinctUntilChanged()
            .cachedIn(viewModelScope)
            .collectLatest { updatePostsPagingData(it) }
    }

    fun loadFilters() {
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

fun SearchUiState.toSearchDto(): SearchDto = SearchDto(value)