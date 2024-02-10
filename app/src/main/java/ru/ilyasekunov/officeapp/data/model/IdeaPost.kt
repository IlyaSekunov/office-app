package ru.ilyasekunov.officeapp.data.model

import java.time.LocalDateTime

data class IdeaPost(
    val id: Long,
    val title: String,
    val content: String,
    val date: LocalDateTime,
    val ideaAuthor: IdeaAuthor,
    val attachedImages: List<ByteArray>,
    val office: Office,
    val likesCount: Int,
    val isLikePressed: Boolean,
    val dislikesCount: Int,
    val isDislikePressed: Boolean,
    val commentsCount: Int,
)