package ru.ilyasekunov.officeapp.ui.ideaauthor

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
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.repository.author.AuthorRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsPagingRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import ru.ilyasekunov.officeapp.exceptions.HttpNotFoundException
import ru.ilyasekunov.officeapp.ui.favouriteideas.IdeasUiState
import ru.ilyasekunov.officeapp.ui.updateDislike
import ru.ilyasekunov.officeapp.ui.updateLike
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
    val authorIdeasUiState = IdeasUiState()

    fun updateLike(idea: IdeaPost) {
        viewModelScope.launch {
            val updatePost = idea.updateLike()
            updateIdea(updatePost)
            if (updatePost.isLikePressed) {
                postsRepository.pressLike(updatePost.id)
            } else {
                postsRepository.removeLike(updatePost.id)
            }
        }
    }

    fun updateDislike(post: IdeaPost) {
        viewModelScope.launch {
            val updatedPost = post.updateDislike()
            updateIdea(updatedPost)
            if (updatedPost.isDislikePressed) {
                postsRepository.pressDislike(updatedPost.id)
            } else {
                postsRepository.removeDislike(updatedPost.id)
            }
        }
    }

    private fun updateIdea(updatedPost: IdeaPost) {
        val authorIdeas = authorIdeasUiState.ideas
        val updatedIdeas = authorIdeas.value.map {
            if (it.id == updatedPost.id) updatedPost
            else it
        }
        authorIdeasUiState.updateIdeas(updatedIdeas)
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
            when {
                ideaAuthorResult.isSuccess -> {
                    val ideaAuthor = ideaAuthorResult.getOrThrow()
                    ideaAuthorUiState = ideaAuthor.toIdeaAuthorUiState()
                    updateIsIdeaAuthorExists(true)
                    updateIsErrorWhileIdeaAuthorLoading(false)
                }

                ideaAuthorResult.exceptionOrNull()!! is HttpNotFoundException -> {
                    updateIsErrorWhileIdeaAuthorLoading(false)
                    updateIsIdeaAuthorExists(false)
                }

                else -> {
                    updateIsErrorWhileIdeaAuthorLoading(true)
                }
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
                    authorIdeasUiState.updateIdeas(it)
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