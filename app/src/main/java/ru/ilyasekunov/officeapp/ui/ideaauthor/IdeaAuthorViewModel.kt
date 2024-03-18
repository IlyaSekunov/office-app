package ru.ilyasekunov.officeapp.ui.ideaauthor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.repository.author.AuthorRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsPagingRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import javax.inject.Inject

data class IdeaAuthorUiState(
    val name: String = "",
    val surname: String = "",
    val photoUrl: String = "",
    val job: String = "",
    val office: Office? = null,
    val isLoading: Boolean = false,
    val isExists: Boolean = true,
    val isErrorWhileLoading: Boolean = false
)

@HiltViewModel
class IdeaAuthorViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val postsPagingRepository: PostsPagingRepository,
    private val authorRepository: AuthorRepository
) : ViewModel() {
    var ideaAuthorUiState by mutableStateOf(IdeaAuthorUiState())
        private set
    private val _authorIdeasUiState: MutableStateFlow<PagingData<IdeaPost>> =
        MutableStateFlow(PagingData.empty())
    val authorIdeasUiState: StateFlow<PagingData<IdeaPost>> get() = _authorIdeasUiState

    fun updateLike(idea: IdeaPost, isPressed: Boolean) {
        viewModelScope.launch {
            val likesCount = if (isPressed) idea.likesCount + 1 else idea.likesCount - 1
            val changedIdea =
                if (idea.isDislikePressed) {
                    idea.copy(
                        isDislikePressed = false,
                        dislikesCount = idea.dislikesCount - 1,
                        isLikePressed = isPressed,
                        likesCount = likesCount
                    )
                } else {
                    idea.copy(
                        isLikePressed = isPressed,
                        likesCount = likesCount
                    )
                }
            updateIdea(changedIdea)
            if (isPressed) {
                postsRepository.pressLike(changedIdea.id)
            } else {
                postsRepository.removeLike(changedIdea.id)
            }
        }
    }

    fun updateDislike(post: IdeaPost, isPressed: Boolean) {
        viewModelScope.launch {
            val dislikesCount = if (isPressed) post.dislikesCount + 1 else post.dislikesCount - 1
            val changedIdea = if (post.isLikePressed) {
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
            updateIdea(changedIdea)
            if (isPressed) {
                postsRepository.pressDislike(changedIdea.id)
            } else {
                postsRepository.removeDislike(changedIdea.id)
            }
        }
    }

    private fun updateIdea(updatedPost: IdeaPost) {
        _authorIdeasUiState.update { pagingData ->
            pagingData.map { if (it.id == updatedPost.id) updatedPost else it }
        }
    }

    private fun updateIsIdeaAuthorLoading(isLoading: Boolean) {
        ideaAuthorUiState = ideaAuthorUiState.copy(isLoading = isLoading)
    }

    private fun updateIsIdeaAuthorExists(isExists: Boolean) {
        ideaAuthorUiState = ideaAuthorUiState.copy(isExists = isExists)
    }

    private fun updateIsErrorWhileIdeaAuthorLoading(isErrorWhileLoading: Boolean) {
        ideaAuthorUiState = ideaAuthorUiState.copy(isErrorWhileLoading = isErrorWhileLoading)
    }

    fun loadIdeaAuthorById(authorId: Long) {
        viewModelScope.launch {
            updateIsIdeaAuthorLoading(true)
            val ideaAuthorResult = authorRepository.ideaAuthorById(authorId)
            if (ideaAuthorResult.isSuccess) {
                val ideaAuthor = ideaAuthorResult.getOrThrow()
                if (ideaAuthor == null) {
                    updateIsIdeaAuthorExists(false)
                } else {
                    updateIsIdeaAuthorExists(true)
                    ideaAuthorUiState = ideaAuthor.toIdeaAuthorUiState()
                }
                updateIsErrorWhileIdeaAuthorLoading(false)
            } else {
                updateIsErrorWhileIdeaAuthorLoading(true)
            }
            updateIsIdeaAuthorLoading(false)
        }
    }

    fun loadIdeasByAuthorId(authorId: Long) {
        viewModelScope.launch {
            postsPagingRepository.postsByAuthorId(authorId)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collectLatest {
                    _authorIdeasUiState.value = it
                }
        }
    }
}

fun IdeaAuthor.toIdeaAuthorUiState(): IdeaAuthorUiState =
    IdeaAuthorUiState(
        name = name,
        surname = surname,
        job = job,
        photoUrl = photo,
        office = office
    )