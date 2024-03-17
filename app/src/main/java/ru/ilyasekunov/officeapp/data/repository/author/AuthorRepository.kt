package ru.ilyasekunov.officeapp.data.repository.author

import ru.ilyasekunov.officeapp.data.model.IdeaAuthor

interface AuthorRepository {
    suspend fun ideaAuthorById(authorId: Long): Result<IdeaAuthor?>
}