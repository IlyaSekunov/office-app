package ru.ilyasekunov.officeapp.data.dto

data class PublishPostDto(
    val title: String,
    val content: String,
    val attachedImages: List<String>
)