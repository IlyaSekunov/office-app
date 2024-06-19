package ru.ilyasekunov.author.datasource

import kotlinx.coroutines.delay
import ru.ilyasekunov.model.IdeaAuthor
import ru.ilyasekunov.network.exceptions.HttpNotFoundException
import ru.ilyasekunov.test.user
import ru.ilyasekunov.test.users
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class FakeAuthorDataSource @Inject constructor(): AuthorDataSource {
    override suspend fun ideaAuthorById(authorId: Long): Result<IdeaAuthor> {
        delay(1200L)
        val user = users.find { it.id == authorId }
        return if (user == null) {
            Result.failure(HttpNotFoundException())
        } else Result.success(user)
    }

    override suspend fun officeEmployees(page: Int, pageSize: Int): Result<List<IdeaAuthor>> {
        delay(1200L)
        val employees = users.filter { user?.office?.id == it.office.id }
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