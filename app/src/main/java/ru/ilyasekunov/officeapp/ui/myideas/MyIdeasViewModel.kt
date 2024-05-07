package ru.ilyasekunov.officeapp.ui.myideas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.filter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.repository.posts.PostsPagingRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import ru.ilyasekunov.officeapp.ui.IdeasUiState
import ru.ilyasekunov.officeapp.ui.home.DeletePostUiState
import javax.inject.Inject

@HiltViewModel
class MyIdeasViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val postsPagingRepository: PostsPagingRepository
) : ViewModel() {
    val myIdeasUiState = IdeasUiState()
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
                .collectLatest { myIdeasUiState.updateIdeas(it) }
        }
    }
}