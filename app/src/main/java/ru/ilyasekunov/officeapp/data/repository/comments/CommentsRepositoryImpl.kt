package ru.ilyasekunov.officeapp.data.repository.comments

import ru.ilyasekunov.officeapp.data.datasource.CommentsDataSource
import ru.ilyasekunov.officeapp.data.dto.CommentDto
import ru.ilyasekunov.officeapp.data.model.Comment

class CommentsRepositoryImpl(
    private val commentsDataSource: CommentsDataSource
) : CommentsRepository {
    override suspend fun commentsByPostId(
        postId: Long,
        page: Int,
        pageSize: Int
    ): Result<List<Comment>> {
        return commentsDataSource.commentsByPostId(postId, page, pageSize)
    }

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
}