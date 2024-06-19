package ru.ilyasekunov.comments.datasource

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.ilyasekunov.comments.api.CommentsApi
import ru.ilyasekunov.common.di.IoDispatcher
import ru.ilyasekunov.dto.CommentDto
import ru.ilyasekunov.model.Comment
import ru.ilyasekunov.network.handleResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CommentsRemoteDataSource @Inject constructor(
    private val commentsApi: CommentsApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
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