package ru.ilyasekunov.officeapp.ui.office

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.repository.auth.AuthRepository
import ru.ilyasekunov.officeapp.data.repository.author.AuthorsPagingRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsPagingRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import ru.ilyasekunov.officeapp.exceptions.HttpForbiddenException
import ru.ilyasekunov.officeapp.ui.PagingDataUiState
import ru.ilyasekunov.officeapp.ui.home.CurrentUserUiState
import ru.ilyasekunov.officeapp.ui.home.DeletePostUiState
import ru.ilyasekunov.officeapp.ui.updateDislike
import ru.ilyasekunov.officeapp.ui.updateLike
import javax.inject.Inject

@HiltViewModel
class MyOfficeViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val postsPagingRepository: PostsPagingRepository,
    private val authorPagingRepository: AuthorsPagingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    val suggestedIdeasUiState = PagingDataUiState<IdeaPost>()
    val ideasInProgressUiState = PagingDataUiState<IdeaPost>()
    val implementedIdeasUiState = PagingDataUiState<IdeaPost>()
    val officeEmployeesUiState = PagingDataUiState<IdeaAuthor>()
    var currentUserUiState by mutableStateOf(CurrentUserUiState())
        private set
    var deletePostUiState by mutableStateOf(DeletePostUiState())
        private set

    init {
        loadData()
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

    fun deletePostResultShown() {
        deletePostUiState = deletePostUiState.copy(
            isError = false,
            isSuccess = false
        )
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

    private fun updatePost(updatedPost: IdeaPost) {
        updateSuggestedIdeasPost(updatedPost)
        updateIdeasInProgressPost(updatedPost)
        updateImplementedIdeasPost(updatedPost)
    }

    private fun loadData() {
        loadCurrentUser()
        loadSuggestedIdeas()
        loadIdeasInProgress()
        loadImplementedIdeas()
        loadOfficeEmployees()
    }

    private fun loadOfficeEmployees() {
        viewModelScope.launch {
            authorPagingRepository.officeEmployees()
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collectLatest {
                    officeEmployeesUiState.updateData(it)
                }
        }
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
                isUnauthorized = exception is HttpForbiddenException,
                isErrorWhileLoading = result.isFailure && exception !is HttpForbiddenException
            )
        }
    }

    private fun loadSuggestedIdeas() {
        viewModelScope.launch {
            postsPagingRepository.suggestedIdeas()
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collectLatest {
                    suggestedIdeasUiState.updateData(it)
                }
        }
    }

    private fun loadIdeasInProgress() {
        viewModelScope.launch {
            postsPagingRepository.ideasInProgress()
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collectLatest {
                    ideasInProgressUiState.updateData(it)
                }
        }
    }

    private fun loadImplementedIdeas() {
        viewModelScope.launch {
            postsPagingRepository.implementedIdeas()
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collectLatest {
                    implementedIdeasUiState.updateData(it)
                }
        }
    }

    private fun updateSuggestedIdeasPost(updatedPost: IdeaPost) {
        suggestedIdeasUiState.updateEntity(updatedPost) { it.id == updatedPost.id }
    }

    private fun updateIdeasInProgressPost(updatedPost: IdeaPost) {
        ideasInProgressUiState.updateEntity(updatedPost) { it.id == updatedPost.id }
    }

    private fun updateImplementedIdeasPost(updatedPost: IdeaPost) {
        implementedIdeasUiState.updateEntity(updatedPost) { it.id == updatedPost.id }
    }
}