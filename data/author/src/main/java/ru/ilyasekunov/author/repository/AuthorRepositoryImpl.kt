package ru.ilyasekunov.author.repository

import ru.ilyasekunov.author.datasource.AuthorDataSource
import ru.ilyasekunov.model.IdeaAuthor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AuthorRepositoryImpl @Inject constructor(
    private val authorDataSource: AuthorDataSource
) : AuthorRepository {
    override suspend fun ideaAuthorById(authorId: Long): Result<IdeaAuthor> {
        return authorDataSource.ideaAuthorById(authorId)
    }
}