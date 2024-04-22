package ru.ilyasekunov.officeapp.ui.editidea

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.dto.EditPostDto
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.repository.images.ImagesRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import ru.ilyasekunov.officeapp.ui.components.AttachedImage
import ru.ilyasekunov.officeapp.ui.imagepickers.ImagePickerDefaults
import javax.inject.Inject

data class EditIdeaUiState(
    val postId: Long = -1,
    val title: String = "",
    val content: String = "",
    val attachedImages: List<AttachedImage> = emptyList(),
    val isLoading: Boolean = false,
    val isPublished: Boolean = false,
    val isNetworkError: Boolean = false
)

@HiltViewModel
class EditIdeaViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val imagesRepository: ImagesRepository
) : ViewModel() {
    var editIdeaUiState by mutableStateOf(EditIdeaUiState())
        private set

    fun updateTitle(title: String) {
        editIdeaUiState = editIdeaUiState.copy(title = title)
    }

    fun updateContent(content: String) {
        editIdeaUiState = editIdeaUiState.copy(content = content)
    }

    private fun attachImage(image: AttachedImage) {
        editIdeaUiState = editIdeaUiState.copy(
            attachedImages = editIdeaUiState.attachedImages + image
        )
    }

    private fun attachImage(image: Uri) {
        if (attachedImagesCount() < ImagePickerDefaults.MAX_ATTACH_IMAGES) {
            viewModelScope.launch {
                synchronized(editIdeaUiState) {
                    val attachedImages = editIdeaUiState.attachedImages
                    val imageId = attachedImages.maxOf { it.id } + 1
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
        editIdeaUiState = editIdeaUiState.copy(
            attachedImages = editIdeaUiState.attachedImages - image
        )
    }

    fun loadPostById(postId: Long) {
        viewModelScope.launch {
            updateIsLoading(true)
            val ideaPostResult = postsRepository.findPostById(postId)
            if (ideaPostResult.isSuccess) {
                val ideaPost = ideaPostResult.getOrThrow()
                editIdeaUiState = ideaPost.toEditIdeaUiState()
                updateIsNetworkError(false)
            } else {
                updateIsNetworkError(true)
            }
            updateIsLoading(false)
        }
    }

    fun editPost() {
        viewModelScope.launch {
            updateIsLoading(true)
            val uploadedImagesUrlsResult = uploadImagesFromUris()
            if (uploadedImagesUrlsResult.isFailure) {
                updateIsNetworkError(true)
                updateIsPublished(false)
                updateIsLoading(false)
                return@launch
            }

            val uploadedImagesUrls = uploadedImagesUrlsResult.getOrThrow()
            val editPostDto = editIdeaUiState.toEditPostDto(uploadedImagesUrls)
            val editPostResult = postsRepository.editPostById(editIdeaUiState.postId, editPostDto)
            if (editPostResult.isSuccess) {
                updateIsNetworkError(false)
                updateIsPublished(true)
            } else {
                updateIsNetworkError(true)
                updateIsPublished(false)
            }
            updateIsPublished(true)
        }
    }

    private suspend fun uploadImagesFromUris(): Result<List<String>> {
        val newAttachedImages = editIdeaUiState.attachedImages.newAttachedImages()
        val newAttachedImagesUrls = ArrayList<String>()
        newAttachedImages.forEach {
            val imageUrlResponse = imagesRepository.uploadImage(it)
            if (imageUrlResponse.isSuccess) {
                val imageUrl = imageUrlResponse.getOrThrow()
                newAttachedImagesUrls += imageUrl
            } else {
                return Result.failure(imageUrlResponse.exceptionOrNull()!!)
            }
        }
        val oldAttachedImagesUrls = editIdeaUiState.attachedImages.oldAttachedImages()
        return Result.success(newAttachedImagesUrls + oldAttachedImagesUrls)
    }

    private fun updateIsLoading(isLoading: Boolean) {
        editIdeaUiState = editIdeaUiState.copy(isLoading = isLoading)
    }

    private fun updateIsPublished(isPublished: Boolean) {
        editIdeaUiState = editIdeaUiState.copy(isPublished = isPublished)
    }

    private fun updateIsNetworkError(isNetworkError: Boolean) {
        editIdeaUiState = editIdeaUiState.copy(isNetworkError = isNetworkError)
    }

    private fun List<AttachedImage>.oldAttachedImages(): List<String> = asSequence()
        .filter { it.image is String }
        .map { it.image as String }
        .toList()

    private fun List<AttachedImage>.newAttachedImages(): List<Uri> = asSequence()
        .filter { it.image is Uri }
        .map { it.image as Uri }
        .toList()

    private fun attachedImagesCount() = editIdeaUiState.attachedImages.size
}

fun IdeaPost.toEditIdeaUiState(): EditIdeaUiState =
    EditIdeaUiState(
        postId = id,
        title = title,
        content = content,
        attachedImages = attachedImages.mapIndexed { index, image ->
            AttachedImage(
                id = index,
                image = image
            )
        }
    )

fun EditIdeaUiState.toEditPostDto(imagesUrls: List<String>) =
    EditPostDto(
        title = title,
        content = content,
        attachedImages = imagesUrls
    )