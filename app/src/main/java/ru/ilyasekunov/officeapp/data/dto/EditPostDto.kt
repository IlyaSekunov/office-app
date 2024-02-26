package ru.ilyasekunov.officeapp.data.dto

data class EditPostDto(
    val title: String,
    val content: String,
    val attachedImages: List<String>
)