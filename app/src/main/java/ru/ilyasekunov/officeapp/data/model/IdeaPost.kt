package ru.ilyasekunov.officeapp.data.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class IdeaPost(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("date")
    val date: LocalDateTime,
    @SerializedName("ideaAuthor")
    val ideaAuthor: IdeaAuthor,
    @SerializedName("attachedImages")
    val attachedImages: List<String>,
    @SerializedName("office")
    val office: Office,
    @SerializedName("likesCount")
    val likesCount: Int,
    @SerializedName("isLikePressed")
    val isLikePressed: Boolean,
    @SerializedName("dislikesCount")
    val dislikesCount: Int,
    @SerializedName("isDislikePressed")
    val isDislikePressed: Boolean,
    @SerializedName("commentsCount")
    val commentsCount: Int,
    @SerializedName("isImplemented")
    val isImplemented: Boolean,
    @SerializedName("isInProgress")
    val isInProgress: Boolean,
    @SerializedName("isSuggestedToMyOffice")
    val isSuggestedToMyOffice: Boolean
)