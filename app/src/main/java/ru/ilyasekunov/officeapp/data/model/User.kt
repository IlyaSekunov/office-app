package ru.ilyasekunov.officeapp.data.model

data class User(
    val id: Long,
    val email: String,
    val password: String,
    val name: String,
    val surname: String,
    val job: String,
    val photo: Any? = null,
    val office: Office
)