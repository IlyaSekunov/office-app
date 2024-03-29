package ru.ilyasekunov.officeapp.data.model

data class User(
    val id: Long,
    val email: String,
    val name: String,
    val surname: String,
    val job: String,
    val photo: String,
    val office: Office
)