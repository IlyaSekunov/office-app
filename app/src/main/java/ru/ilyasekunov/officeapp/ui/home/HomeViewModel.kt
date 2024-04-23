package ru.ilyasekunov.officeapp.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.dto.FiltersDto
import ru.ilyasekunov.officeapp.data.dto.SearchDto
import ru.ilyasekunov.officeapp.data.dto.SearchPostsDto
import ru.ilyasekunov.officeapp.data.model.Filters
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.SortingCategory
import ru.ilyasekunov.officeapp.data.model.User
import ru.ilyasekunov.officeapp.data.repository.auth.AuthRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsPagingRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import ru.ilyasekunov.officeapp.exceptions.HttpForbiddenException
import ru.ilyasekunov.officeapp.ui.favouriteideas.IdeasUiState
import ru.ilyasekunov.officeapp.ui.filters.FiltersUiState
import ru.ilyasekunov.officeapp.ui.filters.FiltersUiStateHolder
import javax.inject.Inject

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
    val postsUiState = IdeasUiState()
    val filtersUiStateHolder = FiltersUiStateHolder(
        initialFiltersUiState = FiltersUiState(),
        coroutineScope = viewModelScope,
        loadFiltersRequest = postsRepository::filters,
        onUpdateFiltersState = ::loadPosts
    )
    var searchUiState by mutableStateOf(SearchUiState())
        private set
    var currentUserUiState by mutableStateOf(CurrentUserUiState())
        private set

    init {
        loadCurrentUser()
        loadPosts()
    }

    fun updateSearchValue(searchValue: String) {
        searchUiState = searchUiState.copy(value = searchValue)
        loadPosts()
    }

    fun updateLike(post: IdeaPost) {
        viewModelScope.launch {
            val isLikePressed = !post.isLikePressed
            val likesCount = if (isLikePressed) post.likesCount + 1 else post.likesCount - 1
            val changedPost = if (post.isDislikePressed) {
                post.copy(
                    isDislikePressed = false,
                    dislikesCount = post.dislikesCount - 1,
                    isLikePressed = isLikePressed,
                    likesCount = likesCount
                )
            } else {
                post.copy(
                    isLikePressed = isLikePressed,
                    likesCount = likesCount
                )
            }
            updatePost(changedPost)
            if (isLikePressed) {
                postsRepository.pressLike(changedPost.id)
            } else {
                postsRepository.removeLike(changedPost.id)
            }
        }
    }

    fun updateDislike(post: IdeaPost) {
        viewModelScope.launch {
            val isDislikePressed = !post.isDislikePressed
            val dislikesCount =
                if (isDislikePressed) post.dislikesCount + 1 else post.dislikesCount - 1
            val changedPost = if (post.isLikePressed) {
                post.copy(
                    isLikePressed = false,
                    likesCount = post.likesCount - 1,
                    isDislikePressed = isDislikePressed,
                    dislikesCount = dislikesCount
                )
            } else {
                post.copy(
                    isDislikePressed = isDislikePressed,
                    dislikesCount = dislikesCount
                )
            }
            updatePost(changedPost)
            if (isDislikePressed) {
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
            if (!filtersUiStateHolder.isLoaded) {
                filtersUiStateHolder.loadFilters().join()
            }
            if (filtersUiStateHolder.isLoaded) {
                loadPostSuspending()
            }
        }
    }

    private fun updatePost(updatedPost: IdeaPost) {
        val postsPagingData = postsUiState.ideas.value
        val updatedPostsPagingData = postsPagingData.map {
            if (it.id == updatedPost.id) updatedPost
            else it
        }
        postsUiState.updateIdeas(updatedPostsPagingData)
    }

    private fun removePost(post: IdeaPost) {
        val postsPagingData = postsUiState.ideas.value
        val updatedPostsPagingData = postsPagingData.filter { it.id != post.id }
        postsUiState.updateIdeas(updatedPostsPagingData)
    }

    private fun updateIsCurrentUserLoading(isLoading: Boolean) {
        currentUserUiState = currentUserUiState.copy(isLoading = isLoading)
    }

    private fun updateIsErrorWhileUserLoading(isErrorWhileLoading: Boolean) {
        currentUserUiState = currentUserUiState.copy(
            isErrorWhileLoading = isErrorWhileLoading
        )
    }

    private fun updateIsUserUnauthorized(isUnauthorized: Boolean) {
        currentUserUiState = currentUserUiState.copy(isUnauthorized = isUnauthorized)
    }

    private fun updateUser(user: User?) {
        currentUserUiState = currentUserUiState.copy(user = user)
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            updateIsCurrentUserLoading(true)
            val userInfoResult = authRepository.userInfo()
            when {
                userInfoResult.isSuccess -> {
                    val user = userInfoResult.getOrThrow()
                    updateIsErrorWhileUserLoading(false)
                    updateIsUserUnauthorized(false)
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
            searchPostsDto = SearchPostsDto(
                filtersDto = filtersUiStateHolder.filtersUiState.toFiltersDto(),
                searchDto = searchUiState.toSearchDto()
            ),
        )
            .distinctUntilChanged()
            .cachedIn(viewModelScope)
            .collectLatest { postsUiState.updateIdeas(it) }
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