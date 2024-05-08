package ru.ilyasekunov.officeapp.ui.ideaauthor

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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
import ru.ilyasekunov.officeapp.ui.IdeasUiState
import ru.ilyasekunov.officeapp.ui.updateDislike
import ru.ilyasekunov.officeapp.ui.updateLike

@Immutable
data class IdeaAuthorUiState(
    val name: String = "",
    val surname: String = "",
    val photoUrl: String = "",
    val job: String = "",
    val office: Office? = null,
    val isLoading: Boolean = true,
    val isExists: Boolean = true,
    val isErrorWhileLoading: Boolean = false
)

@HiltViewModel(assistedFactory = IdeaAuthorViewModel.Factory::class)
class IdeaAuthorViewModel @AssistedInject constructor(
    @Assisted private val authorId: Long,
    private val postsRepository: PostsRepository,
    private val postsPagingRepository: PostsPagingRepository,
    private val authorRepository: AuthorRepository
) : ViewModel() {
    var ideaAuthorUiState by mutableStateOf(IdeaAuthorUiState())
        private set
    val authorIdeasUiState = IdeasUiState()

    init {
        loadData()
    }

    fun loadData() {
        loadIdeaAuthor()
        loadAuthorsIdeas()
    }

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

    private fun loadIdeaAuthor() {
        viewModelScope.launch {
            ideaAuthorUiState = ideaAuthorUiState.copy(isLoading = true)
            refreshIdeaAuthor()
            ideaAuthorUiState = ideaAuthorUiState.copy(isLoading = false)
        }
    }

    suspend fun refreshIdeaAuthor() {
        authorRepository.ideaAuthorById(authorId).also { result ->
            when {
                result.isSuccess -> {
                    val ideaAuthor = result.getOrThrow()
                    ideaAuthorUiState = ideaAuthor.toIdeaAuthorUiState()
                }

                result.exceptionOrNull()!! is HttpNotFoundException -> {
                    ideaAuthorUiState = ideaAuthorUiState.copy(
                        isErrorWhileLoading = false,
                        isExists = false
                    )
                }

                else -> {
                    ideaAuthorUiState = ideaAuthorUiState.copy(isErrorWhileLoading = true)
                }
            }
        }
    }

    private fun loadAuthorsIdeas() {
        viewModelScope.launch {
            postsPagingRepository.postsByAuthorId(authorId)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collectLatest {
                    authorIdeasUiState.updateIdeas(it)
                }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(authorId: Long): IdeaAuthorViewModel
    }
}

fun IdeaAuthor.toIdeaAuthorUiState(): IdeaAuthorUiState =
    IdeaAuthorUiState(
        name = name,
        surname = surname,
        job = job,
        photoUrl = photo,
        office = office,
        isLoading = false,
        isExists = true,
        isErrorWhileLoading = false
    )