package ru.ilyasekunov.dto

import com.google.gson.annotations.SerializedName

data class RegistrationForm(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("userInfo")
    val userInfo: UserDto
)