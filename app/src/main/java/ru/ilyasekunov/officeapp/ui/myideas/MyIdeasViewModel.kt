package ru.ilyasekunov.officeapp.ui.myideas

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
import javax.inject.Inject

@HiltViewModel
class MyIdeasViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val postsPagingRepository: PostsPagingRepository
) : ViewModel() {
    val myIdeasUiState = IdeasUiState()

    init {
        loadMyIdeas()
    }

    fun deletePost(post: IdeaPost) {
        viewModelScope.launch {
            val deletePostResult = postsRepository.deletePostById(post.id)
            if (deletePostResult.isSuccess) {
                removePost(post)
            }
        }
    }

    private fun removePost(post: IdeaPost) {
        val postsPagingData = myIdeasUiState.ideas.value
        val updatedPostsPagingData = postsPagingData.filter { it.id != post.id }
        myIdeasUiState.updateIdeas(updatedPostsPagingData)
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