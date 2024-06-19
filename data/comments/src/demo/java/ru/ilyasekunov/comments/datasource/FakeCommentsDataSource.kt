package ru.ilyasekunov.comments.datasource

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.ilyasekunov.dto.CommentDto
import ru.ilyasekunov.model.Comment
import ru.ilyasekunov.model.CommentsSortingFilters
import ru.ilyasekunov.test.comments
import ru.ilyasekunov.test.ideaAuthor
import java.time.LocalDateTime
import javax.inject.Inject

internal class FakeCommentsDataSource @Inject constructor(): CommentsDataSource {
    private val lock = Mutex()

    override suspend fun commentsByPostId(
        postId: Long,
        sortingFilterId: Int,
        page: Int,
        pageSize: Int
    ): Result<List<Comment>> {
        delay(3000L)
        val sortedComments = when (sortingFilterId) {
            CommentsSortingFilters.NEW.id -> comments.sortedByDescending { it.date }
            CommentsSortingFilters.OLD.id -> comments.sortedBy { it.date }
            CommentsSortingFilters.POPULAR.id -> comments.sortedByDescending { it.likesCount + it.dislikesCount }
            CommentsSortingFilters.UNPOPULAR.id -> comments.sortedBy { it.likesCount + it.dislikesCount }
            else -> comments
        }

        val firstPostIndex = (page - 1) * pageSize
        val lastPostIndex = firstPostIndex + pageSize

        if (firstPostIndex > sortedComments.lastIndex) {
            return Result.success(emptyList())
        }

        if (lastPostIndex > sortedComments.lastIndex) {
            return Result.success(
                sortedComments.subList(
                    fromIndex = firstPostIndex,
                    toIndex = sortedComments.lastIndex + 1
                ).toList()
            )
        }

        return Result.success(
            sortedComments.subList(
                fromIndex = firstPostIndex,
                toIndex = lastPostIndex
            ).toList()
        )
    }

    override suspend fun pressLike(postId: Long, commentId: Long): Result<Unit> {
        val comment = comments.find { it.id == commentId }!!
        val commentIndex = comments.indexOf(comment)

        lock.withLock {
            comments[commentIndex] = comment.copy(
                isLikePressed = true,
                likesCount = comment.likesCount + 1
            )
        }

        if (comment.isDislikePressed) {
            removeDislike(postId, commentId)
        }

        return Result.success(Unit)
    }

    override suspend fun removeLike(postId: Long, commentId: Long): Result<Unit> {
        val comment = comments.find { it.id == commentId }!!
        val commentIndex = comments.indexOf(comment)

        lock.withLock {
            comments[commentIndex] = comment.copy(
                isLikePressed = false,
                likesCount = comment.likesCount - 1
            )
        }

        return Result.success(Unit)
    }

    override suspend fun pressDislike(postId: Long, commentId: Long): Result<Unit> {
        val comment = comments.find { it.id == commentId }!!
        val commentIndex = comments.indexOf(comment)

        lock.withLock {
            comments[commentIndex] = comment.copy(
                isDislikePressed = true,
                dislikesCount = comment.dislikesCount + 1
            )
        }

        if (comment.isLikePressed) {
            removeLike(postId, commentId)
        }

        return Result.success(Unit)
    }

    override suspend fun removeDislike(postId: Long, commentId: Long): Result<Unit> {
        val comment = comments.find { it.id == commentId }!!
        val commentIndex = comments.indexOf(comment)

        lock.withLock {
            comments[commentIndex] = comment.copy(
                isDislikePressed = false,
                dislikesCount = comment.dislikesCount - 1
            )
        }

        return Result.success(Unit)
    }

    override suspend fun sendComment(postId: Long, commentDto: CommentDto): Result<Unit> {
        delay(3000L)

        comments += commentDto.toComment()

        return Result.success(Unit)
    }

    override suspend fun editComment(
        postId: Long,
        commentId: Long,
        commentDto: CommentDto
    ): Result<Unit> {
        delay(3000L)

        val oldComment = comments.find { it.id == commentId }

        oldComment?.let {
            val editedComment = it.copy(
                content = commentDto.content,
                attachedImage = commentDto.attachedImage
            )
            comments[comments.indexOf(oldComment)] = editedComment
        }

        return Result.success(Unit)
    }

    override suspend fun deleteComment(postId: Long, commentId: Long): Result<Unit> {
        delay(3000L)

        val commentToDelete = comments.find { it.id == commentId }

        commentToDelete?.let {
            comments -= it
        }

        return Result.success(Unit)
    }
}

private fun CommentDto.toComment() =
    synchronized(comments) {
        val commentId = if (comments.isEmpty()) 0 else comments.maxOf { it.id } + 1
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