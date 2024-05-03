package ru.ilyasekunov.officeapp.data.datasource.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.ilyasekunov.officeapp.data.api.CommentsApi
import ru.ilyasekunov.officeapp.data.datasource.CommentsDataSource
import ru.ilyasekunov.officeapp.data.dto.CommentDto
import ru.ilyasekunov.officeapp.data.model.Comment

class CommentsRemoteDataSource(
    private val commentsApi: CommentsApi,
    private val ioDispatcher: CoroutineDispatcher
) : CommentsDataSource {
    override suspend fun commentsByPostId(
        postId: Long,
        sortingFilterId: Int,
        page: Int,
        pageSize: Int
    ): Result<List<Comment>> = withContext(ioDispatcher) {
        handleResponse { commentsApi.commentsByPostId(postId, sortingFilterId, page, pageSize) }
    }

    override suspend fun pressLike(postId: Long, commentId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            handleResponse { commentsApi.pressLike(postId, commentId) }
        }

    override suspend fun removeLike(postId: Long, commentId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            handleResponse { commentsApi.removeLike(postId, commentId) }
        }

    override suspend fun pressDislike(postId: Long, commentId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            handleResponse { commentsApi.pressDislike(postId, commentId) }
        }

    override suspend fun removeDislike(postId: Long, commentId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            handleResponse { commentsApi.removeDislike(postId, commentId) }
        }

    override suspend fun sendComment(postId: Long, commentDto: CommentDto): Result<Unit> =
        withContext(ioDispatcher) {
            handleResponse { commentsApi.sendComment(postId, commentDto) }
        }

    override suspend fun editComment(
        postId: Long,
        commentId: Long,
        commentDto: CommentDto
    ): Result<Unit> = withContext(ioDispatcher) {
        handleResponse { commentsApi.editComment(postId, commentId, commentDto) }
    }

    override suspend fun deleteComment(postId: Long, commentId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            handleResponse { commentsApi.deleteComment(postId, commentId) }
        }
}