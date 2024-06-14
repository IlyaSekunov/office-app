package ru.ilyasekunov.dto

import com.google.gson.annotations.SerializedName

data class RefreshTokenDto(
    @SerializedName("refreshToken")
    val refreshToken: String
)