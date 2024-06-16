package ru.ilyasekunov.author.repository

import ru.ilyasekunov.model.IdeaAuthor

interface AuthorRepository {
    suspend fun ideaAuthorById(authorId: Long): Result<IdeaAuthor>
}