package ru.ilyasekunov.officeapp.ui.home.editidea

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

data class AttachedImage(
    val id: Int,
    val image: Any
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
        viewModelScope.launch {
            synchronized(editIdeaUiState) {
                val attachedImages = editIdeaUiState.attachedImages
                val imageId = attachedImages.maxOf { it.id } + 1
                val attachedImage = AttachedImage(id = imageId, image = image)
                attachImage(attachedImage)
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
                ideaPost?.let {
                    editIdeaUiState = it.toEditIdeaUiState()
                }
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
            val uploadedImages = uploadImagesFromUris()
            if (editIdeaUiState.isNetworkError) {
                updateIsLoading(false)
                return@launch
            }
            val editPostDto = EditPostDto(
                title = editIdeaUiState.title,
                content = editIdeaUiState.content,
                attachedImages = uploadedImages
            )
            postsRepository.editPostById(editIdeaUiState.postId, editPostDto)
            updateIsLoading(false)
            updateIsNetworkError(false)
            updateIsPublished(true)
        }
    }

    private suspend fun uploadImagesFromUris(): List<String> {
        val imagesToConvert = editIdeaUiState.attachedImages.filter { it.image is Uri }
        val imagesUrls = ArrayList<String>()
        imagesToConvert.forEach {
            val imageUrlResponse = imagesRepository.uploadImage(it.image as Uri)
            if (imageUrlResponse.isSuccess) {
                val imageUrl = imageUrlResponse.getOrThrow()
                imagesUrls += imageUrl
            } else {
                updateIsNetworkError(true)
                return emptyList()
            }
        }
        return imagesUrls
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