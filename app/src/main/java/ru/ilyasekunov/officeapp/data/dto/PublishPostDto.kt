package ru.ilyasekunov.officeapp.data.dto

import com.google.gson.annotations.SerializedName

data class PublishPostDto(
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("attachedImages")
    val attachedImages: List<String>
)