package ru.ilyasekunov.officeapp.data.model

data class IdeaAuthor(
    val id: Long,
    val name: String,
    val surname: String,
    val job: String,
    val photo: Any? = null
)