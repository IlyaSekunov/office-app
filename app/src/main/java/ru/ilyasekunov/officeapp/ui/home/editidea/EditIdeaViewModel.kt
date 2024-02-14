package ru.ilyasekunov.officeapp.ui.home.editidea

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.dto.EditPostDto
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import javax.inject.Inject

data class EditIdeaUiState(
    val postId: Long,
    val title: String = "",
    val content: String = "",
    val attachedImages: List<AttachedImage> = emptyList()
) {
    companion object {
        val Empty = EditIdeaUiState(
            postId = -1,
            content = "",
            attachedImages = emptyList()
        )
    }
}

data class AttachedImage(
    val id: Int,
    val image: Any
)

@HiltViewModel
class EditIdeaViewModel @Inject constructor(
    private val postsRepository: PostsRepository
): ViewModel() {
    var editIdeaUiState by mutableStateOf(EditIdeaUiState.Empty)
        private set
    var isLoading by mutableStateOf(false)
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

    fun attachImage(image: ByteArray) {
        viewModelScope.launch {
            synchronized(editIdeaUiState) {
                val imageId = if (editIdeaUiState.attachedImages.isNotEmpty()) {
                    editIdeaUiState.attachedImages.maxOf { it.id } + 1
                } else 0
                val attachedImage = AttachedImage(id = imageId, image = image)
                if (!editIdeaUiState.attachedImages.contains(attachedImage)) {
                    attachImage(attachedImage)
                }
            }
        }
    }

    fun removeImage(image: AttachedImage) {
        editIdeaUiState = editIdeaUiState.copy(
            attachedImages = editIdeaUiState.attachedImages - image
        )
    }

    fun loadPostById(postId: Long) {
        viewModelScope.launch {
            isLoading = true
            val ideaPost = postsRepository.findPostById(postId)
            ideaPost?.let {
                editIdeaUiState = it.toEditIdeaUiState()
            }
            isLoading = false
        }
    }

    fun editPost() {
        viewModelScope.launch {
            isLoading = true
            postsRepository.editPostById(editIdeaUiState.postId, editIdeaUiState.toEditPostDto())
            isLoading = false
        }
    }
}

fun EditIdeaUiState.toEditPostDto(): EditPostDto =
    EditPostDto(
        title = title,
        content = content,
        attachedImages = attachedImages.map { it.image }
    )

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