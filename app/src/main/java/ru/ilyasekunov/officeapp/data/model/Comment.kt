package ru.ilyasekunov.officeapp.data.model

import java.time.LocalDateTime

data class Comment(
    val id: Long,
    val author: IdeaAuthor,
    val content: String,
    val attachedImage: String? = null,
    val date: LocalDateTime,
    val isLikePressed: Boolean,
    val likesCount: Int,
    val isDislikePressed: Boolean,
    val dislikesCount: Int
)

enum class CommentsSortingFilters(val id: Int) {
    NEW(1), OLD(2), POPULAR(3), UNPOPULAR(4)
}