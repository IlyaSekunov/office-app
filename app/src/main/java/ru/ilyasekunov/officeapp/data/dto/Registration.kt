package ru.ilyasekunov.officeapp.data.dto

data class RegistrationForm(
    val email: String,
    val password: String,
    val userInfo: UserInfoForm
)

data class UserInfoForm(
    val name: String,
    val surname: String,
    val job: String,
    val photo: String?,
    val officeId: Int
)