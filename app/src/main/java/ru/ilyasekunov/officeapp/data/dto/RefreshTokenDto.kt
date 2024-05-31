package ru.ilyasekunov.officeapp.data.dto

import com.google.gson.annotations.SerializedName

data class RefreshTokenDto(
    @SerializedName("refreshToken")
    val refreshToken: String
)