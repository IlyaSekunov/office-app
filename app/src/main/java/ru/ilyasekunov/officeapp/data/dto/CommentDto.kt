package ru.ilyasekunov.officeapp.data.dto

import com.google.gson.annotations.SerializedName

data class CommentDto(
    @SerializedName("content")
    val content: String,
    @SerializedName("attachedImage")
    val attachedImage: String? = null
)