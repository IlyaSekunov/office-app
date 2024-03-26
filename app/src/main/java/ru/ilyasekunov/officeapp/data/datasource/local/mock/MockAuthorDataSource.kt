package ru.ilyasekunov.officeapp.data.datasource.local.mock

import ru.ilyasekunov.officeapp.data.datasource.AuthorDataSource
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor
import ru.ilyasekunov.officeapp.exceptions.HttpNotFoundException

class MockAuthorDataSource : AuthorDataSource {
    override suspend fun ideaAuthorById(authorId: Long): Result<IdeaAuthor> {
        val user = Users.find { it.id == authorId }
        return if (user == null) {
            Result.failure(HttpNotFoundException())
        } else Result.success(user)
    }
}