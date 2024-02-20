package ru.ilyasekunov.officeapp.ui.home.suggestidea

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.dto.PublishPostDto
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor
import ru.ilyasekunov.officeapp.data.model.User
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import ru.ilyasekunov.officeapp.data.repository.user.UserRepository
import ru.ilyasekunov.officeapp.ui.home.editidea.AttachedImage
import javax.inject.Inject

data class SuggestIdeaUiState(
    val title: String = "",
    val content: String = "",
    val attachedImages: List<AttachedImage> = emptyList()
)

@HiltViewModel
class SuggestIdeaViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    var suggestIdeaUiState by mutableStateOf(SuggestIdeaUiState())
        private set

    fun updateTitle(title: String) {
        suggestIdeaUiState = suggestIdeaUiState.copy(title = title)
    }

    fun updateContent(content: String) {
        suggestIdeaUiState = suggestIdeaUiState.copy(content = content)
    }

    private fun attachImage(image: AttachedImage) {
        suggestIdeaUiState = suggestIdeaUiState.copy(
            attachedImages = suggestIdeaUiState.attachedImages + image
        )
    }

    fun attachImage(image: ByteArray) {
        viewModelScope.launch {
            synchronized(suggestIdeaUiState) {
                val imageId = if (suggestIdeaUiState.attachedImages.isNotEmpty()) {
                    suggestIdeaUiState.attachedImages.maxOf { it.id } + 1
                } else 0
                val attachedImage = AttachedImage(id = imageId, image = image)
                if (!suggestIdeaUiState.attachedImages.contains(attachedImage)) {
                    attachImage(attachedImage)
                }
            }
        }
    }

    fun removeImage(image: AttachedImage) {
        suggestIdeaUiState = suggestIdeaUiState.copy(
            attachedImages = suggestIdeaUiState.attachedImages - image
        )
    }

    fun publishPost() {
        viewModelScope.launch {
            val user = userRepository.user()!!
            val ideaAuthor = user.toIdeaAuthor()
            val publishPostDto = PublishPostDto(
                title = suggestIdeaUiState.title,
                content = suggestIdeaUiState.content,
                author = ideaAuthor,
                office = user.office,
                attachedImages = suggestIdeaUiState.attachedImages.map { (it.image as ByteArray) }
            )
            postsRepository.publishPost(publishPostDto)
        }
    }
}

fun User.toIdeaAuthor(): IdeaAuthor =
    IdeaAuthor(
        id = id,
        name = name,
        surname = surname,
        job = job,
        photo = photo
    )