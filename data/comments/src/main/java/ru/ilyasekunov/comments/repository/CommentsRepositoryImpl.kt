package ru.ilyasekunov.comments.repository

import ru.ilyasekunov.comments.datasource.CommentsDataSource
import ru.ilyasekunov.dto.CommentDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CommentsRepositoryImpl @Inject constructor(
    private val commentsDataSource: CommentsDataSource
) : CommentsRepository {
    override suspend fun pressLike(postId: Long, commentId: Long): Result<Unit> {
        return commentsDataSource.pressLike(postId, commentId)
    }

    override suspend fun removeLike(postId: Long, commentId: Long): Result<Unit> {
        return commentsDataSource.removeLike(postId, commentId)
    }

    override suspend fun pressDislike(postId: Long, commentId: Long): Result<Unit> {
        return commentsDataSource.pressDislike(postId, commentId)
    }

    override suspend fun removeDislike(postId: Long, commentId: Long): Result<Unit> {
        return commentsDataSource.removeDislike(postId, commentId)
    }

    override suspend fun sendComment(postId: Long, commentDto: CommentDto): Result<Unit> {
        return commentsDataSource.sendComment(postId, commentDto)
    }

    override suspend fun editComment(
        postId: Long,
        commentId: Long,
        commentDto: CommentDto
    ): Result<Unit> {
        return commentsDataSource.editComment(postId, commentId, commentDto)
    }

    override suspend fun deleteComment(postId: Long, commentId: Long): Result<Unit> {
        return commentsDataSource.deleteComment(postId, commentId)
    }
}