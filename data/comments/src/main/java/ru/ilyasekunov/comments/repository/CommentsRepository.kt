package ru.ilyasekunov.comments.repository

import ru.ilyasekunov.dto.CommentDto

interface CommentsRepository {
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

    suspend fun editComment(
        postId: Long,
        commentId: Long,
        commentDto: CommentDto
    ): Result<Unit>

    suspend fun deleteComment(
        postId: Long,
        commentId: Long
    ): Result<Unit>
}