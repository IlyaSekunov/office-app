package ru.ilyasekunov.officeapp.ui.home.editingtidea

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.dto.PublishPostDto
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import ru.ilyasekunov.officeapp.data.repository.user.UserRepository
import javax.inject.Inject

data class EditingIdeaUiState(
    val title: String = "",
    val body: String = "",
    val attachedImages: List<AttachedImage> = emptyList()
)

data class AttachedImage(
    val id: Int,
    val image: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttachedImage

        return image.contentEquals(other.image)
    }

    override fun hashCode(): Int {
        return image.contentHashCode()
    }
}

@HiltViewModel
class EditingIdeaViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postsRepository: PostsRepository
): ViewModel() {
    var editingIdeaUiState by mutableStateOf(EditingIdeaUiState())
        private set

    fun updateTitle(title: String) {
        editingIdeaUiState = editingIdeaUiState.copy(title = title)
    }

    fun updateBody(body: String) {
        editingIdeaUiState = editingIdeaUiState.copy(body = body)
    }

    private fun attachImage(image: AttachedImage) {
        editingIdeaUiState = editingIdeaUiState.copy(
            attachedImages = editingIdeaUiState.attachedImages + image
        )
    }

    fun attachImage(image: ByteArray) {
        val imageId = if (editingIdeaUiState.attachedImages.isNotEmpty()) {
            editingIdeaUiState.attachedImages.maxOf { it.id } + 1
        } else 0
        val attachedImage = AttachedImage(id = imageId, image = image)
        if (!editingIdeaUiState.attachedImages.contains(attachedImage)) {
            attachImage(attachedImage)
        }
    }

    fun removeImage(image: AttachedImage) {
        editingIdeaUiState = editingIdeaUiState.copy(
            attachedImages = editingIdeaUiState.attachedImages - image
        )
    }

    fun publishPost() {
        viewModelScope.launch {
            val user = userRepository.findUser()!!
            val author = IdeaAuthor(
                id = user.id,
                name = user.name,
                surname = user.surname,
                job = user.job,
                photo = user.photo
            )
            val publishPostDto = PublishPostDto(
                title = editingIdeaUiState.title,
                content = editingIdeaUiState.body,
                author = author,
                office = user.office,
                attachedImages = editingIdeaUiState.attachedImages.map { it.image }
            )
            postsRepository.publishPost(publishPostDto)
        }
    }
}