package ru.ilyasekunov.officeapp.data.datasource.local.mock

import ru.ilyasekunov.officeapp.data.datasource.AuthorDataSource
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor

class MockAuthorDataSource : AuthorDataSource {
    override suspend fun ideaAuthorById(authorId: Long): Result<IdeaAuthor?> {
        val user = Users.find { it.id == authorId }
        return Result.success(user)
    }
}