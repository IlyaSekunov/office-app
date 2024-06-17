package ru.ilyasekunov.myideas

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
import ru.ilyasekunov.home.DeletePostUiState
import ru.ilyasekunov.model.IdeaPost
import ru.ilyasekunov.posts.repository.PostsPagingRepository
import ru.ilyasekunov.posts.repository.PostsRepository
import ru.ilyasekunov.ui.PagingDataUiState
import javax.inject.Inject

@HiltViewModel
class MyIdeasViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val postsPagingRepository: PostsPagingRepository
) : ViewModel() {
    val myIdeasUiState = PagingDataUiState<IdeaPost>()
    var deleteIdeaUiState by mutableStateOf(DeletePostUiState())
        private set

    init {
        loadMyIdeas()
    }

    fun deletePostResultShown() {
        deleteIdeaUiState = deleteIdeaUiState.copy(
            isError = false,
            isSuccess = false
        )
    }

    fun deletePost(post: IdeaPost) {
        viewModelScope.launch {
            deleteIdeaUiState = deleteIdeaUiState.copy(isLoading = true)
            postsRepository.deletePostById(post.id).also { result ->
                deleteIdeaUiState = deleteIdeaUiState.copy(
                    isLoading = false,
                    isError = result.isFailure,
                    isSuccess = result.isSuccess
                )
            }
        }
    }

    private fun loadMyIdeas() {
        viewModelScope.launch {
            postsPagingRepository.myIdeas()
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collectLatest { myIdeasUiState.updateData(it) }
        }
    }
}