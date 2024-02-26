package ru.ilyasekunov.officeapp.data.dto

data class UserDto(
    val name: String,
    val surname: String,
    val job: String,
    val photo: String? = null,
    val officeId: Int
)
