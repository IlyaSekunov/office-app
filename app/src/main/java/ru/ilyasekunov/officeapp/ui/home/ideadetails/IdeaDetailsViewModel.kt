package ru.ilyasekunov.officeapp.ui.home.ideadetails

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
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.repository.comments.CommentsPagingRepository
import ru.ilyasekunov.officeapp.data.repository.comments.CommentsRepository
import ru.ilyasekunov.officeapp.data.repository.images.ImagesRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import ru.ilyasekunov.officeapp.exceptions.HttpNotFoundException
import ru.ilyasekunov.officeapp.ui.components.AttachedImage
import ru.ilyasekunov.officeapp.ui.components.SendingMessageUiState
import javax.inject.Inject

data class IdeaPostUiState(
    val ideaPost: IdeaPost? = null,
    val isLoading: Boolean = false,
    val isErrorWhileLoading: Boolean = false,
    val postExists: Boolean = true
)

@HiltViewModel
class IdeaDetailsViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val imagesRepository: ImagesRepository,
    private val commentsRepository: CommentsRepository,
    private val commentsPagingRepository: CommentsPagingRepository
) : ViewModel() {
    private var _commentsUiState: MutableStateFlow<PagingData<Comment>> =
        MutableStateFlow(PagingData.empty())
    val commentsUiState: StateFlow<PagingData<Comment>> get() = _commentsUiState
    var ideaPostUiState by mutableStateOf(IdeaPostUiState())
        private set
    var sendingMessageUiState by mutableStateOf(SendingMessageUiState())
        private set

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

    private fun updateComment(comment: Comment) {
        _commentsUiState.update { pagingData ->
            pagingData.map { if (it.id == comment.id) comment else it }
        }
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
            val isPressed = !post.isLikePressed
            val likesCount = if (isPressed) post.likesCount + 1 else post.likesCount - 1
            val changedPost =
                if (post.isDislikePressed) {
                    post.copy(
                        isDislikePressed = false,
                        dislikesCount = post.dislikesCount - 1,
                        isLikePressed = isPressed,
                        likesCount = likesCount
                    )
                } else {
                    post.copy(
                        isLikePressed = isPressed,
                        likesCount = likesCount
                    )
                }
            updatePost(changedPost)
            if (isPressed) {
                postsRepository.pressLike(post.id)
            } else {
                postsRepository.removeLike(post.id)
            }
        }
    }

    fun updatePostDislike() {
        viewModelScope.launch {
            val post = ideaPostUiState.ideaPost!!
            val isPressed = !post.isDislikePressed
            val dislikesCount = if (isPressed) post.dislikesCount + 1 else post.dislikesCount - 1
            val changedPost = if (post.isLikePressed) {
                post.copy(
                    isLikePressed = false,
                    likesCount = post.likesCount - 1,
                    isDislikePressed = isPressed,
                    dislikesCount = dislikesCount
                )
            } else {
                post.copy(
                    isDislikePressed = isPressed,
                    dislikesCount = dislikesCount
                )
            }
            updatePost(changedPost)
            if (isPressed) {
                postsRepository.pressDislike(post.id)
            } else {
                postsRepository.removeDislike(post.id)
            }
        }
    }

    fun updateCommentLike(comment: Comment) {
        viewModelScope.launch {
            val isLikePressed = !comment.isLikePressed
            val likesCount = if (isLikePressed) comment.likesCount + 1 else comment.likesCount - 1
            val changedComment =
                if (comment.isDislikePressed) {
                    comment.copy(
                        isDislikePressed = false,
                        dislikesCount = comment.dislikesCount - 1,
                        isLikePressed = isLikePressed,
                        likesCount = likesCount
                    )
                } else {
                    comment.copy(
                        isLikePressed = isLikePressed,
                        likesCount = likesCount
                    )
                }
            updateComment(changedComment)
            val postId = ideaPostUiState.ideaPost!!.id
            if (isLikePressed) {
                commentsRepository.pressLike(postId, changedComment.id)
            } else {
                commentsRepository.removeLike(postId, changedComment.id)
            }
        }
    }

    fun updateCommentDislike(comment: Comment) {
        viewModelScope.launch {
            val isDislikePressed = !comment.isDislikePressed
            val dislikesCount =
                if (isDislikePressed) comment.dislikesCount + 1 else comment.dislikesCount - 1
            val changedComment = if (comment.isLikePressed) {
                comment.copy(
                    isLikePressed = false,
                    likesCount = comment.likesCount - 1,
                    isDislikePressed = isDislikePressed,
                    dislikesCount = dislikesCount
                )
            } else {
                comment.copy(
                    isDislikePressed = isDislikePressed,
                    dislikesCount = dislikesCount
                )
            }
            updateComment(changedComment)
            val postId = ideaPostUiState.ideaPost!!.id
            if (isDislikePressed) {
                commentsRepository.pressDislike(postId, changedComment.id)
            } else {
                commentsRepository.removeDislike(postId, changedComment.id)
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

                val commentDto = CommentDto(
                    content = sendingMessageUiState.message,
                    attachedImage = uploadedImageResult.getOrThrow()
                )
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
            commentsPagingRepository.commentsByPostId(postId)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {
                    _commentsUiState.value = it
                }
        }
    }

    suspend fun loadPostByIdSuspending(postId: Long) {
        updateIsPostLoading(true)
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
        updateIsPostLoading(false)
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