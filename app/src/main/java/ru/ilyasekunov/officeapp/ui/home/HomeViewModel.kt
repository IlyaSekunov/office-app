package ru.ilyasekunov.officeapp.ui.home

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
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
import ru.ilyasekunov.officeapp.ui.IdeasUiState
import ru.ilyasekunov.officeapp.ui.filters.FiltersUiState
import ru.ilyasekunov.officeapp.ui.filters.FiltersUiStateHolder
import ru.ilyasekunov.officeapp.ui.updateDislike
import ru.ilyasekunov.officeapp.ui.updateLike
import javax.inject.Inject

@Immutable
data class OfficeFilterUiState(
    val office: Office,
    val isSelected: Boolean = false
)

@Immutable
data class SortingFiltersUiState(
    val filters: List<SortingCategory> = emptyList(),
    val selected: SortingCategory? = null
)

@Immutable
data class CurrentUserUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isErrorWhileLoading: Boolean = false,
    val isUnauthorized: Boolean = false
)

@Immutable
data class SearchUiState(val value: String = "")

@Immutable
data class SuggestIdeaToMyOfficeUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isSuccess: Boolean = false
)

@Immutable
data class DeletePostUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isSuccess: Boolean = false
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
    var suggestIdeaToMyOfficeUiState by mutableStateOf(SuggestIdeaToMyOfficeUiState())
        private set
    var deletePostUiState by mutableStateOf(DeletePostUiState())
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
            val updatedPost = post.updateLike()
            updatePost(updatedPost)
            if (updatedPost.isLikePressed) {
                postsRepository.pressLike(updatedPost.id)
            } else {
                postsRepository.removeLike(updatedPost.id)
            }
        }
    }

    fun updateDislike(post: IdeaPost) {
        viewModelScope.launch {
            val updatedPost = post.updateDislike()
            updatePost(updatedPost)
            if (updatedPost.isDislikePressed) {
                postsRepository.pressDislike(updatedPost.id)
            } else {
                postsRepository.removeDislike(updatedPost.id)
            }
        }
    }

    fun deletePost(post: IdeaPost) {
        viewModelScope.launch {
            deletePostUiState = deletePostUiState.copy(isLoading = true)
            postsRepository.deletePostById(post.id).also { result ->
                deletePostUiState = deletePostUiState.copy(
                    isLoading = false,
                    isSuccess = result.isSuccess,
                    isError = result.isFailure
                )
            }
        }
    }

    fun deletePostResultShown() {
        deletePostUiState = deletePostUiState.copy(
            isError = false,
            isSuccess = false
        )
    }

    private fun loadPosts() {
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

    fun loadCurrentUser() {
        viewModelScope.launch {
            currentUserUiState = currentUserUiState.copy(isLoading = true)
            refreshCurrentUser()
            currentUserUiState = currentUserUiState.copy(isLoading = false)
        }
    }

    suspend fun refreshCurrentUser() {
        authRepository.userInfo().also { result ->
            val exception = result.exceptionOrNull()
            currentUserUiState = currentUserUiState.copy(
                user = result.getOrNull(),
                isErrorWhileLoading = result.isFailure && exception !is HttpForbiddenException,
                isUnauthorized = exception is HttpForbiddenException
            )
        }
    }

    fun suggestIdeaToMyOffice(idea: IdeaPost) {
        viewModelScope.launch {
            suggestIdeaToMyOfficeUiState = suggestIdeaToMyOfficeUiState.copy(isLoading = true)
            postsRepository.suggestIdeaToMyOffice(idea.id).also { result ->
                suggestIdeaToMyOfficeUiState = suggestIdeaToMyOfficeUiState.copy(
                    isLoading = false,
                    isSuccess = result.isSuccess,
                    isError = result.isFailure
                )
            }
        }
    }

    fun suggestIdeaToMyOfficeResultShown() {
        suggestIdeaToMyOfficeUiState = suggestIdeaToMyOfficeUiState.copy(
            isSuccess = false,
            isError = false
        )
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
        },
        isLoaded = true
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