package ru.ilyasekunov.author.datasource

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.ilyasekunov.author.api.AuthorApi
import ru.ilyasekunov.common.di.IoDispatcher
import ru.ilyasekunov.model.IdeaAuthor
import ru.ilyasekunov.network.handleResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AuthorRemoteDataSource @Inject constructor(
    private val authorApi: AuthorApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AuthorDataSource {
    override suspend fun ideaAuthorById(authorId: Long): Result<IdeaAuthor> =
        withContext(ioDispatcher) {
            handleResponse { authorApi.ideaAuthorById(authorId) }
        }

    override suspend fun officeEmployees(page: Int, pageSize: Int): Result<List<IdeaAuthor>> =
        withContext(ioDispatcher) {
            handleResponse { authorApi.officeEmployees(page, pageSize) }
        }
}