package ru.ilyasekunov.officeapp.data.datasource

import ru.ilyasekunov.officeapp.data.model.IdeaAuthor

interface AuthorDataSource {
    suspend fun ideaAuthorById(authorId: Long): Result<IdeaAuthor>
}