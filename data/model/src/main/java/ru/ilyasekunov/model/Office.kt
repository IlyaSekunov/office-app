package ru.ilyasekunov.model

import com.google.gson.annotations.SerializedName

data class Office(
    @SerializedName("id")
    val id: Int,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("address")
    val address: String
)