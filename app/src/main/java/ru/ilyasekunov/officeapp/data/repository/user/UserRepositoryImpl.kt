package ru.ilyasekunov.officeapp.data.repository.user

import ru.ilyasekunov.officeapp.data.datasource.UserDataSource
import ru.ilyasekunov.officeapp.data.dto.UserDto

class UserRepositoryImpl(
    private val userDatasource: UserDataSource
) : UserRepository {
    override suspend fun saveChanges(user: UserDto): Result<Unit> {
        return userDatasource.saveChanges(user)
    }
}