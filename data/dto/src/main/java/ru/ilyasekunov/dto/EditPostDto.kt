package ru.ilyasekunov.dto

import com.google.gson.annotations.SerializedName

data class EditPostDto(
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("attachedImages")
    val attachedImages: List<String>
)