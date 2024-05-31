package ru.ilyasekunov.officeapp.data.datasource.local.mock

import kotlinx.coroutines.delay
import ru.ilyasekunov.officeapp.data.datasource.AuthorDataSource
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor
import ru.ilyasekunov.officeapp.exceptions.HttpNotFoundException

class MockAuthorDataSource : AuthorDataSource {
    override suspend fun ideaAuthorById(authorId: Long): Result<IdeaAuthor> {
        delay(1200L)
        val user = Users.find { it.id == authorId }
        return if (user == null) {
            Result.failure(HttpNotFoundException())
        } else Result.success(user)
    }

    override suspend fun officeEmployees(page: Int, pageSize: Int): Result<List<IdeaAuthor>> {
        delay(1200L)
        val employees = Users.filter { User?.office?.id == it.office.id }
        val firstPostIndex = (page - 1) * pageSize
        val lastPostIndex = firstPostIndex + pageSize
        if (firstPostIndex > employees.lastIndex) {
            return Result.success(emptyList())
        }
        if (lastPostIndex > employees.lastIndex) {
            return Result.success(
                employees.subList(
                    fromIndex = firstPostIndex,
                    toIndex = employees.lastIndex + 1
                )
            )
        }
        return Result.success(
            employees.subList(
                fromIndex = firstPostIndex,
                toIndex = lastPostIndex
            )
        )
    }
}