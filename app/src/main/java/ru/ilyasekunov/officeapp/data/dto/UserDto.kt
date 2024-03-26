package ru.ilyasekunov.officeapp.data.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    val name: String,
    val surname: String,
    val job: String,
    val photo: String? = null,
    @SerializedName("office")
    val officeId: Int
)
