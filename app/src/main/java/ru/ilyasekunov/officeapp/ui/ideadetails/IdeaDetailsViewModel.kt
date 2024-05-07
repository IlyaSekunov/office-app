package ru.ilyasekunov.officeapp.ui.ideadetails

import android.net.Uri
import androidx.compose.runtime.Immutable
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.dto.CommentDto
import ru.ilyasekunov.officeapp.data.model.Comment
import ru.ilyasekunov.officeapp.data.model.CommentsSortingFilters
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.model.User
import ru.ilyasekunov.officeapp.data.repository.auth.AuthRepository
import ru.ilyasekunov.officeapp.data.repository.comments.CommentsPagingRepository
import ru.ilyasekunov.officeapp.data.repository.comments.CommentsRepository
import ru.ilyasekunov.officeapp.data.repository.images.ImagesRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import ru.ilyasekunov.officeapp.exceptions.HttpNotFoundException
import ru.ilyasekunov.officeapp.ui.components.AttachedImage
import ru.ilyasekunov.officeapp.ui.components.SendingMessageUiState
import ru.ilyasekunov.officeapp.ui.home.CurrentUserUiState
import ru.ilyasekunov.officeapp.ui.updateDislike
import ru.ilyasekunov.officeapp.ui.updateLike
import javax.inject.Inject

@Immutable
data class IdeaPostUiState(
    val ideaPost: IdeaPost? = null,
    val isLoading: Boolean = true,
    val isErrorWhileLoading: Boolean = false,
    val postExists: Boolean = true
)

class CommentsUiState {
    private var _comments = MutableStateFlow(PagingData.empty<Comment>())
    val comments get() = _comments.asStateFlow()
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

@Immutable
data class CommentDeletingUiState(
    val isDeleting: Boolean = false,
    val isDeleted: Boolean = false
)

@HiltViewModel
class IdeaDetailsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val postsRepository: PostsRepository,
    private val imagesRepository: ImagesRepository,
    private val commentsRepository: CommentsRepository,
    private val commentsPagingRepository: CommentsPagingRepository
) : ViewModel() {
    var currentUserUiState by mutableStateOf(CurrentUserUiState())
        private set
    var ideaPostUiState by mutableStateOf(IdeaPostUiState())
        private set
    var sendingMessageUiState by mutableStateOf(SendingMessageUiState())
        private set
    val commentsUiState = CommentsUiState()
    var currentEditableComment by mutableStateOf<Comment?>(null)
        private set
    var commentDeletingUiState by mutableStateOf<CommentDeletingUiState?>(null)
        private set

    init {
        loadCurrentUser()
    }

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
            val imageId = nextAttachedImagesId()
            val attachedImage = AttachedImage(imageId, uri)
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

    fun startEditComment(comment: Comment) {
        currentEditableComment = comment
        sendingMessageUiState = comment.toSendingMessageUiState()
    }

    fun stopEditComment() {
        currentEditableComment = null
        clearSendingUiState()
    }

    private fun clearSendingUiState() {
        sendingMessageUiState = sendingMessageUiState.copy(
            message = "",
            attachedImages = emptyList()
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

    private fun updateIsCommentPublished(isPublished: Boolean) {
        sendingMessageUiState = sendingMessageUiState.copy(isPublished = isPublished)
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

    fun sendComment() {
        if (isMessageValid()) {
            viewModelScope.launch {
                updateIsSendingMessageStateLoading(true)
                uploadAttachedImage().also { result ->
                    if (result.isFailure) {
                        updateIsErrorWhileSendingComment(true)
                        updateIsCommentPublished(false)
                        updateIsSendingMessageStateLoading(false)
                        return@launch
                    }

                    val uploadedImageUrl = result.getOrThrow()
                    uploadComment(uploadedImageUrl)
                }
            }
        }
    }

    fun deleteComment(comment: Comment) {
        viewModelScope.launch {
            commentDeletingUiState = CommentDeletingUiState()
            updateCommentIsDeleting(true)
            val postId = ideaPostUiState.ideaPost!!.id
            commentsRepository.deleteComment(postId, comment.id).also { result ->
                updateCommentIsDeleting(false)
                updateCommentIsDeleted(result.isSuccess)
            }
        }
    }

    private fun updateCommentIsDeleting(isDeleting: Boolean) {
        commentDeletingUiState = commentDeletingUiState?.copy(isDeleting = isDeleting)
    }

    private fun updateCommentIsDeleted(isDeleted: Boolean) {
        commentDeletingUiState = commentDeletingUiState?.copy(isDeleted = isDeleted)
    }

    private suspend fun uploadComment(attachedImageUrl: String?) {
        val commentDto = sendingMessageUiState.toCommentDto(attachedImageUrl)
        processSendRequest(commentDto).also { result ->
            updateIsSendingMessageStateLoading(false)
            updateIsErrorWhileSendingComment(result.isFailure)
            updateIsCommentPublished(result.isSuccess)
            if (result.isSuccess) {
                clearSendingUiState()
                currentEditableComment = null
            }
        }
    }

    private suspend fun processSendRequest(commentDto: CommentDto): Result<Unit> {
        val ideaPostUiState = ideaPostUiState
        val currentEditableComment = currentEditableComment
        val postId = ideaPostUiState.ideaPost!!.id
        return if (currentEditableComment != null) {
            val commentId = currentEditableComment.id
            commentsRepository.editComment(postId, commentId, commentDto)
        } else {
            commentsRepository.sendComment(postId, commentDto)
        }
    }

    fun loadPostById(postId: Long) {
        viewModelScope.launch {
            updateIsPostLoading(true)
            refreshPostById(postId)
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

    fun loadCurrentUser() {
        viewModelScope.launch {
            updateIsCurrentUserLoading(true)
            refreshCurrentUser()
            updateIsCurrentUserLoading(false)
        }
    }

    private fun updateIsCurrentUserLoading(isLoading: Boolean) {
        currentUserUiState = currentUserUiState.copy(isLoading = isLoading)
    }

    suspend fun refreshCurrentUser() {
        authRepository.userInfo().also { result ->
            updateIsErrorWhileUserLoading(result.isFailure)
            if (result.isSuccess) {
                val user = result.getOrThrow()
                updateUser(user)
            }
        }
    }

    private fun updateIsErrorWhileUserLoading(isErrorWhileLoading: Boolean) {
        currentUserUiState = currentUserUiState.copy(
            isErrorWhileLoading = isErrorWhileLoading
        )
    }

    private fun updateUser(user: User?) {
        currentUserUiState = currentUserUiState.copy(user = user)
    }

    suspend fun refreshPostById(postId: Long) {
        postsRepository.findPostById(postId).also { result ->
            when {
                result.isSuccess -> {
                    updateIsErrorWhilePostsLoading(false)
                    updatePostExists(true)
                    val post = result.getOrThrow()
                    updatePost(post)
                }

                result.exceptionOrNull()!! is HttpNotFoundException -> {
                    updateIsErrorWhilePostsLoading(false)
                    updatePostExists(false)
                }

                else -> {
                    updateIsErrorWhilePostsLoading(true)
                }
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

    private fun nextAttachedImagesId(): Int {
        val attachedImages = sendingMessageUiState.attachedImages
        return if (attachedImages.isEmpty()) 0
        else attachedImages.maxOf { it.id } + 1
    }
}

fun SendingMessageUiState.toCommentDto(uploadedImageUrl: String?) =
    CommentDto(
        content = message,
        attachedImage = uploadedImageUrl
    )

fun Comment.toSendingMessageUiState() =
    SendingMessageUiState(
        message = content,
        attachedImages = if (attachedImage == null) emptyList()
        else listOf(attachedImagesToUiState()),
    )

private fun Comment.attachedImagesToUiState() =
    AttachedImage(
        id = 0,
        image = attachedImage!!
    )