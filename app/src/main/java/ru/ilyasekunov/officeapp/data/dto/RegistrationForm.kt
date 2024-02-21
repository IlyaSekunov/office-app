package ru.ilyasekunov.officeapp.data.dto

data class RegistrationForm(
    val email: String,
    val password: String,
    val userInfo: UserDto
)