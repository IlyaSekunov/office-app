package ru.ilyasekunov.officeapp.ui.editidea

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.dto.EditPostDto
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.repository.images.ImagesRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import ru.ilyasekunov.officeapp.ui.components.AttachedImage
import ru.ilyasekunov.officeapp.ui.imagepickers.ImagePickerDefaults

@Immutable
data class EditIdeaUiState(
    val postId: Long = -1,
    val title: String = "",
    val content: String = "",
    val attachedImages: List<AttachedImage> = emptyList(),
    val isLoading: Boolean = false,
    val isPublished: Boolean = false,
    val isErrorWhilePublishing: Boolean = false,
    val isErrorWhileLoading: Boolean = false
)

@HiltViewModel(assistedFactory = EditIdeaViewModel.Factory::class)
class EditIdeaViewModel @AssistedInject constructor(
    @Assisted private val postId: Long,
    private val postsRepository: PostsRepository,
    private val imagesRepository: ImagesRepository
) : ViewModel() {
    var editIdeaUiState by mutableStateOf(EditIdeaUiState())
        private set

    init {
        loadPost()
    }

    fun updateTitle(title: String) {
        editIdeaUiState = editIdeaUiState.copy(title = title)
    }

    fun updateContent(content: String) {
        editIdeaUiState = editIdeaUiState.copy(content = content)
    }

    fun errorWhilePublishingShown() {
        editIdeaUiState = editIdeaUiState.copy(isErrorWhilePublishing = false)
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
        editIdeaUiState = editIdeaUiState.copy(
            attachedImages = editIdeaUiState.attachedImages - image
        )
    }

    fun loadPost() {
        viewModelScope.launch {
            editIdeaUiState = editIdeaUiState.copy(isLoading = true)
            postsRepository.findPostById(postId).also { result ->
                editIdeaUiState = editIdeaUiState.copy(
                    isLoading = false,
                    isErrorWhileLoading = result.isFailure
                )
                if (result.isSuccess) {
                    val ideaPost = result.getOrThrow()
                    editIdeaUiState = ideaPost.toEditIdeaUiState()
                }
            }
        }
    }

    fun editPost() {
        viewModelScope.launch {
            editIdeaUiState = editIdeaUiState.copy(isLoading = true)
            uploadImagesFromUris().also { result ->
                if (result.isFailure) {
                    editIdeaUiState = editIdeaUiState.copy(
                        isLoading = false,
                        isPublished = false,
                        isErrorWhilePublishing = true
                    )
                    return@launch
                }

                val uploadedImagesUrls = result.getOrThrow()
                editPost(uploadedImagesUrls)
            }
        }
    }

    private suspend fun editPost(uploadedImagesUrls: List<String>) {
        val editPostDto = editIdeaUiState.toEditPostDto(uploadedImagesUrls)
        postsRepository.editPostById(editIdeaUiState.postId, editPostDto).also { result ->
            editIdeaUiState = editIdeaUiState.copy(
                isLoading = false,
                isErrorWhilePublishing = result.isFailure,
                isPublished = result.isSuccess
            )
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

    private fun List<AttachedImage>.oldAttachedImages(): List<String> = asSequence()
        .filter { it.image is String }
        .map { it.image as String }
        .toList()

    private fun List<AttachedImage>.newAttachedImages(): List<Uri> = asSequence()
        .filter { it.image is Uri }
        .map { it.image as Uri }
        .toList()

    private fun attachedImagesCount() = editIdeaUiState.attachedImages.size

    private fun nextAttachedImagesId(): Int {
        val attachedImages = editIdeaUiState.attachedImages
        return if (attachedImages.isEmpty()) 0
        else attachedImages.maxOf { it.id } + 1
    }

    @AssistedFactory
    interface Factory {
        fun create(postId: Long): EditIdeaViewModel
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

fun EditIdeaUiState.toEditPostDto(imagesUrls: List<String>) =
    EditPostDto(
        title = title,
        content = content,
        attachedImages = imagesUrls
    )