package ru.ilyasekunov.officeapp.data.datasource.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.ilyasekunov.officeapp.data.api.UserApi
import ru.ilyasekunov.officeapp.data.datasource.UserDataSource
import ru.ilyasekunov.officeapp.data.dto.UserDto
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.User

class UserRemoteDataSource(
    private val userApi: UserApi,
    private val ioDispatcher: CoroutineDispatcher
) : UserDataSource {
    override suspend fun user(): User? =
        withContext(ioDispatcher) {
            userApi.user()
        }

    override suspend fun saveChanges(userDto: UserDto) =
        withContext(ioDispatcher) {
            userApi.saveChanges(userDto)
        }

    override suspend fun availableOffices(): List<Office> =
        withContext(ioDispatcher) {
            userApi.availableOffices()
        }
}