package ru.ilyasekunov.officeapp.data.datasource.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.ilyasekunov.officeapp.data.api.AuthorApi
import ru.ilyasekunov.officeapp.data.datasource.AuthorDataSource
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor

class AuthorRemoteDataSource(
    private val authorApi: AuthorApi,
    private val ioDispatcher: CoroutineDispatcher
) : AuthorDataSource {
    override suspend fun ideaAuthorById(authorId: Long): Result<IdeaAuthor> =
        withContext(ioDispatcher) {
            handleResponse { authorApi.ideaAuthorById(authorId) }
        }
}