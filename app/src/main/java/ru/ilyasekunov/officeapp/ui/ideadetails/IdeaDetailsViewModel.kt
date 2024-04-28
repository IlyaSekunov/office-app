package ru.ilyasekunov.officeapp.ui.ideadetails

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.dto.CommentDto
import ru.ilyasekunov.officeapp.data.model.Comment
import ru.ilyasekunov.officeapp.data.model.CommentsSortingFilters
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.repository.comments.CommentsPagingRepository
import ru.ilyasekunov.officeapp.data.repository.comments.CommentsRepository
import ru.ilyasekunov.officeapp.data.repository.images.ImagesRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import ru.ilyasekunov.officeapp.exceptions.HttpNotFoundException
import ru.ilyasekunov.officeapp.ui.components.AttachedImage
import ru.ilyasekunov.officeapp.ui.components.SendingMessageUiState
import ru.ilyasekunov.officeapp.ui.updateDislike
import ru.ilyasekunov.officeapp.ui.updateLike
import javax.inject.Inject

data class IdeaPostUiState(
    val ideaPost: IdeaPost? = null,
    val isLoading: Boolean = true,
    val isErrorWhileLoading: Boolean = false,
    val postExists: Boolean = true
)

class CommentsUiState {
    private var _comments: MutableStateFlow<PagingData<Comment>> =
        MutableStateFlow(PagingData.empty())
    val comments: StateFlow<PagingData<Comment>> get() = _comments
    var currentSortingFilter by mutableStateOf(CommentsSortingFilters.NEW)
        private set

    fun updateComment(comment: Comment) {
        _comments.update { pagingData ->
            pagingData.map { if (it.id == comment.id) comment else it }
        }
    }

    fun updateSortingFilter(sortingFilter: CommentsSortingFilters) {
        currentSortingFilter = sortingFilter
    }

    fun updateComments(commentsPagingData: PagingData<Comment>) {
        _comments.value = commentsPagingData
    }
}

@HiltViewModel
class IdeaDetailsViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val imagesRepository: ImagesRepository,
    private val commentsRepository: CommentsRepository,
    private val commentsPagingRepository: CommentsPagingRepository
) : ViewModel() {
    var ideaPostUiState by mutableStateOf(IdeaPostUiState())
        private set
    val commentsUiState = CommentsUiState()
    var sendingMessageUiState by mutableStateOf(SendingMessageUiState())
        private set

    fun updateCommentsSortingFilter(commentsSortingFilter: CommentsSortingFilters) {
        if (commentsUiState.currentSortingFilter != commentsSortingFilter) {
            commentsUiState.updateSortingFilter(commentsSortingFilter)
            loadCommentsByPostId(ideaPostUiState.ideaPost!!.id)
        }
    }

    fun updateMessage(message: String) {
        sendingMessageUiState = sendingMessageUiState.copy(message = message)
    }

    fun attachImage(uri: Uri) {
        synchronized(sendingMessageUiState) {
            val imageId = if (sendingMessageUiState.attachedImages.isEmpty()) 0 else
                sendingMessageUiState.attachedImages.maxOf { it.id } + 1
            val attachedImage = AttachedImage(
                id = imageId,
                image = uri
            )
            sendingMessageUiState = sendingMessageUiState.copy(
                attachedImages = sendingMessageUiState.attachedImages + attachedImage
            )
        }
    }

    fun removeImage(attachedImage: AttachedImage) {
        sendingMessageUiState = sendingMessageUiState.copy(
            attachedImages = sendingMessageUiState.attachedImages - attachedImage
        )
    }

    private fun updateIsPostLoading(isLoading: Boolean) {
        ideaPostUiState = ideaPostUiState.copy(isLoading = isLoading)
    }

    private fun updateIsErrorWhilePostsLoading(isErrorWhileLoading: Boolean) {
        ideaPostUiState = ideaPostUiState.copy(isErrorWhileLoading = isErrorWhileLoading)
    }

    private fun updatePostExists(postExists: Boolean) {
        ideaPostUiState = ideaPostUiState.copy(postExists = postExists)
    }

    private fun updatePost(post: IdeaPost) {
        ideaPostUiState = ideaPostUiState.copy(ideaPost = post)
    }

    private fun updateIsSendingMessageStateLoading(isLoading: Boolean) {
        sendingMessageUiState = sendingMessageUiState.copy(isLoading = isLoading)
    }

    private fun updateIsErrorWhileSendingComment(isErrorWhileSending: Boolean) {
        sendingMessageUiState = sendingMessageUiState.copy(
            isErrorWhileSending = isErrorWhileSending
        )
    }

    fun updatePostLike() {
        viewModelScope.launch {
            val post = ideaPostUiState.ideaPost!!
            val updatedPost = post.updateLike()
            updatePost(updatedPost)
            if (updatedPost.isLikePressed) {
                postsRepository.pressLike(post.id)
            } else {
                postsRepository.removeLike(post.id)
            }
        }
    }

    fun updatePostDislike() {
        viewModelScope.launch {
            val post = ideaPostUiState.ideaPost!!
            val updatedPost = post.updateDislike()
            updatePost(updatedPost)
            if (updatedPost.isDislikePressed) {
                postsRepository.pressDislike(post.id)
            } else {
                postsRepository.removeDislike(post.id)
            }
        }
    }

    fun updateCommentLike(comment: Comment) {
        viewModelScope.launch {
            val updatedComment = comment.updateLike()
            commentsUiState.updateComment(updatedComment)
            val postId = ideaPostUiState.ideaPost!!.id
            if (updatedComment.isLikePressed) {
                commentsRepository.pressLike(postId, updatedComment.id)
            } else {
                commentsRepository.removeLike(postId, updatedComment.id)
            }
        }
    }

    fun updateCommentDislike(comment: Comment) {
        viewModelScope.launch {
            val updatedComment = comment.updateDislike()
            commentsUiState.updateComment(updatedComment)
            val postId = ideaPostUiState.ideaPost!!.id
            if (updatedComment.isDislikePressed) {
                commentsRepository.pressDislike(postId, updatedComment.id)
            } else {
                commentsRepository.removeDislike(postId, updatedComment.id)
            }
        }
    }

    private fun clearSendingUiState() {
        sendingMessageUiState = SendingMessageUiState()
    }

    fun sendComment() {
        if (isMessageValid()) {
            viewModelScope.launch {
                updateIsSendingMessageStateLoading(true)
                val uploadedImageResult = uploadAttachedImage()
                if (uploadedImageResult.isFailure) {
                    updateIsErrorWhileSendingComment(true)
                    updateIsSendingMessageStateLoading(false)
                    return@launch
                }

                val uploadedImageUrl = uploadedImageResult.getOrThrow()
                val commentDto = sendingMessageUiState.toCommentDto(uploadedImageUrl)
                val postId = ideaPostUiState.ideaPost!!.id
                val sendCommentResult = commentsRepository.sendComment(postId, commentDto)
                if (sendCommentResult.isSuccess) {
                    updateIsErrorWhileSendingComment(false)
                    clearSendingUiState()
                } else {
                    updateIsErrorWhileSendingComment(true)
                }
                updateIsSendingMessageStateLoading(false)
            }
        }
    }

    fun loadPostById(postId: Long) {
        viewModelScope.launch {
            updateIsPostLoading(true)
            loadPostByIdSuspending(postId)
            updateIsPostLoading(false)
        }
    }

    fun loadCommentsByPostId(postId: Long) {
        viewModelScope.launch {
            commentsPagingRepository.commentsByPostId(
                postId = postId,
                sortingFilterId = commentsUiState.currentSortingFilter.id
            )
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {
                    commentsUiState.updateComments(it)
                }
        }
    }

    suspend fun loadPostByIdSuspending(postId: Long) {
        val postResult = postsRepository.findPostById(postId)
        when {
            postResult.isSuccess -> {
                updateIsErrorWhilePostsLoading(false)
                updatePostExists(true)
                val post = postResult.getOrThrow()
                updatePost(post)
            }

            postResult.exceptionOrNull()!! is HttpNotFoundException -> {
                updateIsErrorWhilePostsLoading(false)
                updatePostExists(false)
            }

            else -> {
                updateIsErrorWhilePostsLoading(true)
            }
        }
    }

    private suspend fun uploadAttachedImage(): Result<String?> {
        if (sendingMessageUiState.attachedImages.isEmpty()) {
            return Result.success(null)
        }
        val imageToUpload = sendingMessageUiState.attachedImages[0]
        if (imageToUpload.image is Uri) {
            val uploadResult = imagesRepository.uploadImage(imageToUpload.image)
            if (uploadResult.isFailure) {
                return Result.failure(uploadResult.exceptionOrNull()!!)
            }
            return Result.success(uploadResult.getOrThrow())
        }
        if (imageToUpload.image is String) {
            return Result.success(imageToUpload.image)
        }
        return Result.success(null)
    }

    private fun isMessageValid() = sendingMessageUiState.message.isNotBlank()
}

fun SendingMessageUiState.toCommentDto(uploadedImageUrl: String?) =
    CommentDto(
        content = message,
        attachedImage = uploadedImageUrl
    )