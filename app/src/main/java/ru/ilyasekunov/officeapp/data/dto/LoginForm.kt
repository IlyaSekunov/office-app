package ru.ilyasekunov.officeapp.data.dto

import com.google.gson.annotations.SerializedName

data class LoginForm(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)