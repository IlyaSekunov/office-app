package ru.ilyasekunov.officeapp.data.repository.comments

import ru.ilyasekunov.officeapp.data.dto.CommentDto
import ru.ilyasekunov.officeapp.data.model.Comment

interface CommentsRepository {
    suspend fun commentsByPostId(
        postId: Long,
        page: Int,
        pageSize: Int
    ): Result<List<Comment>>

    suspend fun pressLike(
        postId: Long,
        commentId: Long
    ): Result<Unit>

    suspend fun removeLike(
        postId: Long,
        commentId: Long
    ): Result<Unit>

    suspend fun pressDislike(
        postId: Long,
        commentId: Long
    ): Result<Unit>

    suspend fun removeDislike(
        postId: Long,
        commentId: Long
    ): Result<Unit>

    suspend fun sendComment(
        postId: Long,
        commentDto: CommentDto
    ): Result<Unit>
}