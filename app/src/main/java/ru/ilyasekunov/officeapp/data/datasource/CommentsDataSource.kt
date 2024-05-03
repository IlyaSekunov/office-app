package ru.ilyasekunov.officeapp.data.datasource

import ru.ilyasekunov.officeapp.data.dto.CommentDto
import ru.ilyasekunov.officeapp.data.model.Comment

interface CommentsDataSource {
    suspend fun commentsByPostId(
        postId: Long,
        sortingFilterId: Int,
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