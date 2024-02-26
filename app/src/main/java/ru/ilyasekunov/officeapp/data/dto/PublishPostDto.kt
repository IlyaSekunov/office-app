package ru.ilyasekunov.officeapp.data.dto

import ru.ilyasekunov.officeapp.data.model.IdeaAuthor
import ru.ilyasekunov.officeapp.data.model.Office

data class PublishPostDto(
    val title: String,
    val content: String,
    val author: IdeaAuthor,
    val office: Office,
    val attachedImages: List<String>
)