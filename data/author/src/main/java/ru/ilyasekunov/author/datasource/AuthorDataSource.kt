package ru.ilyasekunov.author.datasource

import ru.ilyasekunov.model.IdeaAuthor

internal interface AuthorDataSource {
    suspend fun ideaAuthorById(authorId: Long): Result<IdeaAuthor>
    suspend fun officeEmployees(
        page: Int,
        pageSize: Int
    ): Result<List<IdeaAuthor>>
}