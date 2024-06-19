package ru.ilyasekunov.user.repository

import ru.ilyasekunov.dto.UserDto
import ru.ilyasekunov.user.datasource.UserDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UserRepositoryImpl @Inject constructor(
    private val userDatasource: UserDataSource
) : UserRepository {
    override suspend fun saveChanges(user: UserDto): Result<Unit> {
        return userDatasource.saveChanges(user)
    }
}