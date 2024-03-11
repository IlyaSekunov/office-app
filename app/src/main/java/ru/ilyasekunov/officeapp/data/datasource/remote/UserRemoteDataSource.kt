package ru.ilyasekunov.officeapp.data.datasource.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.ilyasekunov.officeapp.data.api.UserApi
import ru.ilyasekunov.officeapp.data.datasource.UserDataSource
import ru.ilyasekunov.officeapp.data.dto.UserDto

class UserRemoteDataSource(
    private val userApi: UserApi,
    private val ioDispatcher: CoroutineDispatcher
) : UserDataSource {
    override suspend fun saveChanges(userDto: UserDto): Result<Unit> =
        withContext(ioDispatcher) {
            handleResponse { userApi.saveChanges(userDto) }
        }
}