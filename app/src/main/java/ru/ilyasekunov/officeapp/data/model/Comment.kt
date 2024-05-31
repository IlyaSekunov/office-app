package ru.ilyasekunov.officeapp.data.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class Comment(
    @SerializedName("id")
    val id: Long,
    @SerializedName("author")
    val author: IdeaAuthor,
    @SerializedName("content")
    val content: String,
    @SerializedName("attachedImage")
    val attachedImage: String? = null,
    @SerializedName("date")
    val date: LocalDateTime,
    @SerializedName("isLikePressed")
    val isLikePressed: Boolean,
    @SerializedName("likesCount")
    val likesCount: Int,
    @SerializedName("isDislikePressed")
    val isDislikePressed: Boolean,
    @SerializedName("dislikesCount")
    val dislikesCount: Int
)

enum class CommentsSortingFilters(val id: Int) {
    NEW(1), OLD(2), POPULAR(3), UNPOPULAR(4)
}