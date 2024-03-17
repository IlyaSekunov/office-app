package ru.ilyasekunov.officeapp.data.repository.author

import ru.ilyasekunov.officeapp.data.datasource.AuthorDataSource
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor

class AuthorRepositoryImpl(
    private val authorDataSource: AuthorDataSource
) : AuthorRepository {
    override suspend fun ideaAuthorById(authorId: Long): Result<IdeaAuthor?> {
        return authorDataSource.ideaAuthorById(authorId)
    }
}