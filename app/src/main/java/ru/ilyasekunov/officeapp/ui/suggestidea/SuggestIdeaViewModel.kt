package ru.ilyasekunov.officeapp.ui.suggestidea

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.dto.PublishPostDto
import ru.ilyasekunov.officeapp.data.repository.images.ImagesRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import ru.ilyasekunov.officeapp.ui.components.AttachedImage
import ru.ilyasekunov.officeapp.ui.imagepickers.ImagePickerDefaults
import javax.inject.Inject

@Immutable
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

    fun networkErrorShown() {
        suggestIdeaUiState = suggestIdeaUiState.copy(
            isNetworkError = false
        )
    }

    private fun attachImage(image: AttachedImage) {
        suggestIdeaUiState = suggestIdeaUiState.copy(
            attachedImages = suggestIdeaUiState.attachedImages + image
        )
    }

    private fun attachImage(image: Uri) {
        if (attachedImagesCount() < ImagePickerDefaults.MAX_ATTACH_IMAGES) {
            viewModelScope.launch {
                synchronized(suggestIdeaUiState) {
                    val imageId = nextAttachedImagesId()
                    val attachedImage = AttachedImage(id = imageId, image = image)
                    attachImage(attachedImage)
                }
            }
        }
    }

    fun attachImages(images: List<Uri>) {
        images.forEach { attachImage(it) }
    }

    fun removeImage(image: AttachedImage) {
        suggestIdeaUiState = suggestIdeaUiState.copy(
            attachedImages = suggestIdeaUiState.attachedImages - image
        )
    }

    fun publishPost() {
        viewModelScope.launch {
            updateIsLoading(true)
            val uploadedImagesResult = uploadImagesFromUris()
            if (uploadedImagesResult.isFailure) {
                updateIsPublished(false)
                updateIsNetworkError(true)
                updateIsLoading(false)
                return@launch
            }

            val uploadedImagesUrls = uploadedImagesResult.getOrThrow()
            publishPost(uploadedImagesUrls)
        }
    }

    private suspend fun publishPost(uploadedImagesUrls: List<String>) {
        val publishPostDto = suggestIdeaUiState.toPublishPostDto(uploadedImagesUrls)
        postsRepository.publishPost(publishPostDto).also { result ->
            updateIsLoading(false)
            updateIsNetworkError(result.isFailure)
            updateIsPublished(result.isSuccess)
        }
    }

    private suspend fun uploadImagesFromUris(): Result<List<String>> {
        val imagesUrls = suggestIdeaUiState.attachedImages.map {
            val imageUrlResult = imagesRepository.uploadImage(it.image as Uri)
            if (imageUrlResult.isSuccess) {
                val uploadedImageUrl = imageUrlResult.getOrThrow()
                uploadedImageUrl
            } else {
                return Result.failure(imageUrlResult.exceptionOrNull()!!)
            }
        }
        return Result.success(imagesUrls)
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

    private fun attachedImagesCount() = suggestIdeaUiState.attachedImages.size

    private fun nextAttachedImagesId(): Int {
        val attachedImages = suggestIdeaUiState.attachedImages
        return if (attachedImages.isEmpty()) 0
        else attachedImages.maxOf { it.id } + 1
    }
}

fun SuggestIdeaUiState.toPublishPostDto(uploadedImagesUrls: List<String>) =
    PublishPostDto(
        title = title,
        content = content,
        attachedImages = uploadedImagesUrls
    )