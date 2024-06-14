package ru.ilyasekunov.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Long,
    @SerializedName("email")
    val email: String,
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