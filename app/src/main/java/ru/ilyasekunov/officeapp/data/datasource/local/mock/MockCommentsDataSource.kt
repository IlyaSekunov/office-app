package ru.ilyasekunov.officeapp.data.datasource.local.mock

import okhttp3.internal.toImmutableList
import ru.ilyasekunov.officeapp.data.datasource.CommentsDataSource
import ru.ilyasekunov.officeapp.data.dto.CommentDto
import ru.ilyasekunov.officeapp.data.model.Comment
import java.time.LocalDateTime

class MockCommentsDataSource : CommentsDataSource {
    override suspend fun commentsByPostId(
        postId: Long,
        page: Int,
        pageSize: Int
    ): Result<List<Comment>> {
        val firstPostIndex = (page - 1) * pageSize
        val lastPostIndex = firstPostIndex + pageSize
        if (firstPostIndex > Comments.lastIndex) {
            return Result.success(emptyList())
        }
        if (lastPostIndex > Comments.lastIndex) {
            return Result.success(
                Comments.subList(
                    fromIndex = firstPostIndex,
                    toIndex = Comments.lastIndex + 1
                ).toImmutableList()
            )
        }
        return Result.success(
            Comments.subList(
                fromIndex = firstPostIndex,
                toIndex = lastPostIndex
            ).toImmutableList()
        )
    }

    override suspend fun pressLike(postId: Long, commentId: Long): Result<Unit> {
        val commentIndex = Comments.indexOf(Comments.find { it.id == postId }!!)
        val comment = Comments[commentIndex]
        Comments[commentIndex] = comment.copy(
            isLikePressed = true,
            likesCount = comment.likesCount + 1
        )
        if (comment.isDislikePressed) {
            removeDislike(postId, commentId)
        }
        return Result.success(Unit)
    }

    override suspend fun removeLike(postId: Long, commentId: Long): Result<Unit> {
        val commentIndex = Comments.indexOf(Comments.find { it.id == postId }!!)
        val comment = Comments[commentIndex]
        Comments[commentIndex] = comment.copy(
            isLikePressed = false,
            likesCount = comment.likesCount - 1
        )
        return Result.success(Unit)
    }

    override suspend fun pressDislike(postId: Long, commentId: Long): Result<Unit> {
        val commentIndex = Comments.indexOf(Comments.find { it.id == postId }!!)
        val comment = Comments[commentIndex]
        Comments[commentIndex] = comment.copy(
            isDislikePressed = true,
            dislikesCount = comment.dislikesCount + 1
        )
        if (comment.isLikePressed) {
            removeLike(postId, commentId)
        }
        return Result.success(Unit)
    }

    override suspend fun removeDislike(postId: Long, commentId: Long): Result<Unit> {
        val commentIndex = Comments.indexOf(Comments.find { it.id == postId }!!)
        val comment = Comments[commentIndex]
        Comments[commentIndex] = comment.copy(
            isDislikePressed = false,
            dislikesCount = comment.dislikesCount - 1
        )
        return Result.success(Unit)
    }

    override suspend fun sendComment(postId: Long, commentDto: CommentDto): Result<Unit> {
        Comments += commentDto.toComment()
        return Result.success(Unit)
    }
}

private fun CommentDto.toComment() =
    synchronized(Comments) {
        val commentId = if (Comments.isEmpty()) 0 else Comments.maxOf { it.id } + 1
        Comment(
            id = commentId,
            author = ideaAuthor,
            content = content,
            attachedImage = attachedImage,
            date = LocalDateTime.now(),
            isLikePressed = false,
            likesCount = 0,
            isDislikePressed = false,
            dislikesCount = 0
        )
    }