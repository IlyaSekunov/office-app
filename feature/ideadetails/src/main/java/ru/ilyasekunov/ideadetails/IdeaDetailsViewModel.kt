package ru.ilyasekunov.ideadetails

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.ilyasekunov.auth.repository.AuthRepository
import ru.ilyasekunov.comments.repository.CommentsPagingRepository
import ru.ilyasekunov.comments.repository.CommentsRepository
import ru.ilyasekunov.dto.CommentDto
import ru.ilyasekunov.images.repository.ImagesRepository
import ru.ilyasekunov.model.Comment
import ru.ilyasekunov.model.CommentsSortingFilters
import ru.ilyasekunov.model.IdeaPost
import ru.ilyasekunov.network.exceptions.HttpNotFoundException
import ru.ilyasekunov.posts.repository.PostsRepository
import ru.ilyasekunov.profile.CurrentUserUiState
import ru.ilyasekunov.ui.PagingDataUiState
import ru.ilyasekunov.ui.components.AttachedImage
import ru.ilyasekunov.ui.components.SendMessageUiState
import ru.ilyasekunov.ui.updateDislike
import ru.ilyasekunov.ui.updateLike

@Immutable
data class IdeaPostUiState(
    val ideaPost: IdeaPost? = null,
    val isLoading: Boolean = true,
    val isErrorWhileLoading: Boolean = false,
    val postExists: Boolean = true
)

class CommentsUiState {
    val comments = PagingDataUiState<Comment>()
    var currentSortingFilter by mutableStateOf(CommentsSortingFilters.NEW)
        private set

    fun updateComment(comment: Comment) {
        comments.updateEntity(comment) { it.id == comment.id }
    }

    fun updateSortingFilter(sortingFilter: CommentsSortingFilters) {
        currentSortingFilter = sortingFilter
    }

    fun updateComments(commentsPagingData: PagingData<Comment>) {
        comments.updateData(commentsPagingData)
    }
}

@Immutable
data class DeleteCommentUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isError: Boolean = false
)

@HiltViewModel(assistedFactory = IdeaDetailsViewModel.Factory::class)
class IdeaDetailsViewModel @AssistedInject constructor(
    @Assisted private val postId: Long,
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
    var sendMessageUiState by mutableStateOf(SendMessageUiState())
        private set
    val commentsUiState = CommentsUiState()
    var currentEditableComment by mutableStateOf<Comment?>(null)
        private set
    var deleteCommentUiState by mutableStateOf(DeleteCommentUiState())
        private set
    private val lock = Mutex()

    init {
        loadData()
        observeSortingCommentsFilterState()
    }

    fun updateCommentsSortingFilter(commentsSortingFilter: CommentsSortingFilters) {
        if (commentsUiState.currentSortingFilter != commentsSortingFilter) {
            commentsUiState.updateSortingFilter(commentsSortingFilter)
        }
    }

    fun updateMessage(message: String) {
        sendMessageUiState = sendMessageUiState.copy(message = message)
    }

    fun attachImage(uri: Uri) {
        viewModelScope.launch {
            lock.withLock {
                val imageId = nextAttachedImagesId()
                val attachedImage = AttachedImage(imageId, uri)
                sendMessageUiState = sendMessageUiState.copy(
                    attachedImages = sendMessageUiState.attachedImages + attachedImage
                )
            }
        }
    }

    fun removeImage(attachedImage: AttachedImage) {
        sendMessageUiState = sendMessageUiState.copy(
            attachedImages = sendMessageUiState.attachedImages - attachedImage
        )
    }

    fun startEditComment(comment: Comment) {
        currentEditableComment = comment
        sendMessageUiState = comment.toSendingMessageUiState()
    }

    fun stopEditComment() {
        currentEditableComment = null
        clearSendingUiState()
    }

    private fun clearSendingUiState() {
        sendMessageUiState = sendMessageUiState.copy(
            message = "",
            attachedImages = emptyList()
        )
    }

    fun updatePostLike() {
        viewModelScope.launch {
            val post = ideaPostUiState.ideaPost!!
            val updatedPost = post.updateLike()
            ideaPostUiState = ideaPostUiState.copy(ideaPost = updatedPost)
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
            ideaPostUiState = ideaPostUiState.copy(ideaPost = updatedPost)
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
                sendMessageUiState = sendMessageUiState.copy(isLoading = true)
                uploadAttachedImage().also { result ->
                    if (result.isFailure) {
                        sendMessageUiState = sendMessageUiState.copy(
                            isLoading = false,
                            isPublished = false,
                            isError = true
                        )
                        return@launch
                    }

                    val uploadedImageUrl = result.getOrThrow()
                    uploadComment(uploadedImageUrl)
                }
            }
        }
    }

    private suspend fun uploadComment(attachedImageUrl: String?) {
        val commentDto = sendMessageUiState.toCommentDto(attachedImageUrl)
        processSendRequest(commentDto).also { result ->
            sendMessageUiState = sendMessageUiState.copy(
                isLoading = false,
                isPublished = result.isSuccess,
                isError = result.isFailure
            )
            if (result.isSuccess) {
                clearSendingUiState()
                currentEditableComment = null
            }
        }
    }

    fun publishCommentResultShown() {
        sendMessageUiState = sendMessageUiState.copy(
            isError = false,
            isPublished = false
        )
    }

    fun deleteComment(comment: Comment) {
        viewModelScope.launch {
            deleteCommentUiState = deleteCommentUiState.copy(isLoading = true)
            commentsRepository.deleteComment(postId, comment.id).also { result ->
                deleteCommentUiState = deleteCommentUiState.copy(
                    isLoading = false,
                    isSuccess = result.isSuccess,
                    isError = result.isFailure
                )
            }
        }
    }

    fun deleteCommentResultShown() {
        deleteCommentUiState = deleteCommentUiState.copy(
            isError = false,
            isSuccess = false
        )
    }

    private suspend fun processSendRequest(commentDto: CommentDto): Result<Unit> {
        val currentEditableComment = currentEditableComment
        return if (currentEditableComment != null) {
            val commentId = currentEditableComment.id
            commentsRepository.editComment(postId, commentId, commentDto)
        } else {
            commentsRepository.sendComment(postId, commentDto)
        }
    }

    fun loadData() {
        loadCurrentUser()
        loadPost()
    }

    private fun loadPost() {
        viewModelScope.launch {
            ideaPostUiState = ideaPostUiState.copy(isLoading = true)
            refreshPost()
            ideaPostUiState = ideaPostUiState.copy(isLoading = false)
        }
    }

    private fun observeSortingCommentsFilterState() {
        viewModelScope.launch {
            snapshotFlow {
                commentsPagingRepository.commentsByPostId(
                    postId = postId,
                    sortingFilterId = commentsUiState.currentSortingFilter.id
                )
                    .distinctUntilChanged()
                    .cachedIn(viewModelScope)
            }
                .flatMapLatest { it }
                .collectLatest { commentsUiState.updateComments(it) }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            currentUserUiState = currentUserUiState.copy(isLoading = true)
            refreshCurrentUser()
            currentUserUiState = currentUserUiState.copy(isLoading = false)
        }
    }

    suspend fun refreshCurrentUser() {
        authRepository.userInfo().also { result ->
            currentUserUiState = currentUserUiState.copy(isErrorWhileLoading = result.isFailure)
            if (result.isSuccess) {
                val user = result.getOrThrow()
                currentUserUiState = currentUserUiState.copy(user = user)
            }
        }
    }

    suspend fun refreshPost() {
        postsRepository.findPostById(postId).also { result ->
            when {
                result.isSuccess -> {
                    val post = result.getOrThrow()
                    ideaPostUiState = ideaPostUiState.copy(
                        ideaPost = post,
                        isErrorWhileLoading = false,
                        postExists = true
                    )
                }

                result.exceptionOrNull()!! is HttpNotFoundException -> {
                    ideaPostUiState = ideaPostUiState.copy(
                        isErrorWhileLoading = false,
                        postExists = false
                    )
                }

                else -> {
                    ideaPostUiState = ideaPostUiState.copy(isErrorWhileLoading = true)
                }
            }
        }
    }

    private suspend fun uploadAttachedImage(): Result<String?> {
        if (sendMessageUiState.attachedImages.isEmpty()) {
            return Result.success(null)
        }

        val imageToUpload = sendMessageUiState.attachedImages[0]
        if (imageToUpload.image is Uri) {
            val uploadResult = imagesRepository.uploadImage(imageToUpload.image as Uri)
            if (uploadResult.isFailure) {
                return Result.failure(uploadResult.exceptionOrNull()!!)
            }
            return Result.success(uploadResult.getOrThrow())
        }

        if (imageToUpload.image is String) {
            return Result.success(imageToUpload.image as String)
        }

        return Result.success(null)
    }

    private fun isMessageValid() = sendMessageUiState.message.isNotBlank()

    private fun nextAttachedImagesId(): Int {
        val attachedImages = sendMessageUiState.attachedImages
        return if (attachedImages.isEmpty()) 0
        else attachedImages.maxOf { it.id } + 1
    }

    @AssistedFactory
    interface Factory {
        fun create(postId: Long): IdeaDetailsViewModel
    }
}

fun SendMessageUiState.toCommentDto(uploadedImageUrl: String?) =
    CommentDto(
        content = message,
        attachedImage = uploadedImageUrl
    )

fun Comment.toSendingMessageUiState() =
    SendMessageUiState(
        message = content,
        attachedImages = if (attachedImage == null) emptyList()
        else listOf(attachedImagesToUiState())
    )

private fun Comment.attachedImagesToUiState() =
    AttachedImage(
        id = 0,
        image = attachedImage!!
    )