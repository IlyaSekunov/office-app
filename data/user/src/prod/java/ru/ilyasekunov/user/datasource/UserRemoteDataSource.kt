package ru.ilyasekunov.user.datasource

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.ilyasekunov.common.di.IoDispatcher
import ru.ilyasekunov.dto.UserDto
import ru.ilyasekunov.network.handleResponse
import ru.ilyasekunov.user.api.UserApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UserRemoteDataSource @Inject constructor(
    private val userApi: UserApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserDataSource {
    override suspend fun saveChanges(userDto: UserDto): Result<Unit> =
        withContext(ioDispatcher) {
            handleResponse { userApi.saveChanges(userDto) }
        }
}