package ru.ilyasekunov.officeapp.ui.home.suggestidea

import android.net.Uri
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
import ru.ilyasekunov.officeapp.data.repository.images.ImagesRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import ru.ilyasekunov.officeapp.data.repository.user.UserRepository
import ru.ilyasekunov.officeapp.ui.home.editidea.AttachedImage
import javax.inject.Inject

data class SuggestIdeaUiState(
    val title: String = "",
    val content: String = "",
    val attachedImages: List<AttachedImage> = emptyList(),
    val isLoading: Boolean = false,
    val isPublished: Boolean = false,
    val isNetworkError: Boolean = false
)

@HiltViewModel
class SuggestIdeaViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val userRepository: UserRepository,
    private val imagesRepository: ImagesRepository
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

    fun attachImage(image: Uri) {
        viewModelScope.launch {
            val attachedImages = suggestIdeaUiState.attachedImages
            synchronized(suggestIdeaUiState) {
                val imageId = if (attachedImages.isNotEmpty()) {
                    attachedImages.maxOf { it.id } + 1
                } else 0
                val attachedImage = AttachedImage(id = imageId, image = image)
                attachImage(attachedImage)
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
            updateIsLoading(true)
            val uploadedImages = uploadImagesFromUris()
            if (suggestIdeaUiState.isNetworkError) {
                updateIsLoading(false)
                return@launch
            }

            val user = userRepository.user()!!
            val ideaAuthor = user.toIdeaAuthor()
            val publishPostDto = PublishPostDto(
                title = suggestIdeaUiState.title,
                content = suggestIdeaUiState.content,
                author = ideaAuthor,
                office = user.office,
                attachedImages = uploadedImages
            )
            postsRepository.publishPost(publishPostDto)
            updateIsLoading(false)
            updateIsNetworkError(false)
            updateIsPublished(true)
        }
    }

    private suspend fun uploadImagesFromUris(): List<String> {
        val imagesUrls = ArrayList<String>()
        suggestIdeaUiState.attachedImages.forEach {
            val imageUrlResult = imagesRepository.uploadImage(it.image as Uri)
            if (imageUrlResult.isSuccess) {
                val imageUrl = imageUrlResult.getOrThrow()
                imagesUrls += imageUrl
            } else {
                updateIsNetworkError(true)
                return emptyList()
            }
        }
        return imagesUrls
    }

    private fun updateIsLoading(isLoading: Boolean) {
        suggestIdeaUiState = suggestIdeaUiState.copy(isLoading = isLoading)
    }

    private fun updateIsPublished(isPublished: Boolean) {
        suggestIdeaUiState = suggestIdeaUiState.copy(isPublished = isPublished)
    }

    private fun updateIsNetworkError(isNetworkError: Boolean) {
        suggestIdeaUiState = suggestIdeaUiState.copy(isNetworkError = isNetworkError)
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