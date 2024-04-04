package ru.ilyasekunov.officeapp.data.dto

data class CommentDto(
    val content: String,
    val attachedImage: String? = null
)