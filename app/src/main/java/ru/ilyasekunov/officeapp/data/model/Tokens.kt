package ru.ilyasekunov.officeapp.data.model

import com.google.gson.annotations.SerializedName

data class Tokens(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String
)
