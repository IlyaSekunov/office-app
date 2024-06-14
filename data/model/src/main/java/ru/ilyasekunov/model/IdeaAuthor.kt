package ru.ilyasekunov.model

import com.google.gson.annotations.SerializedName

data class IdeaAuthor(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("surname")
    val surname: String,
    @SerializedName("job")
    val job: String,
    @SerializedName("photo")
    val photo: String,
    @SerializedName("office")
    val office: Office
)