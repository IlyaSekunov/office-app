package ru.ilyasekunov.officeapp.data.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("surname")
    val surname: String,
    @SerializedName("job")
    val job: String,
    @SerializedName("photo")
    val photo: String? = null,
    @SerializedName("office")
    val officeId: Int
)
