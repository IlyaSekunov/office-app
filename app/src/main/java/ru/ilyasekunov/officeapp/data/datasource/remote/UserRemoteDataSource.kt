package ru.ilyasekunov.officeapp.data.datasource.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.ilyasekunov.officeapp.data.api.UserApi
import ru.ilyasekunov.officeapp.data.datasource.UserDataSource
import ru.ilyasekunov.officeapp.data.dto.UserDto
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.User
import ru.ilyasekunov.officeapp.util.toResult

class UserRemoteDataSource(
    private val userApi: UserApi,
    private val ioDispatcher: CoroutineDispatcher
) : UserDataSource {
    override suspend fun user(): Result<User?> =
        withContext(ioDispatcher) {
            userApi.user().toResult()
        }

    override suspend fun saveChanges(userDto: UserDto): Result<Unit> =
        withContext(ioDispatcher) {
            userApi.saveChanges(userDto).toResult()
        }

    override suspend fun availableOffices(): Result<List<Office>> =
        withContext(ioDispatcher) {
            userApi.availableOffices().toResult()
        }
}