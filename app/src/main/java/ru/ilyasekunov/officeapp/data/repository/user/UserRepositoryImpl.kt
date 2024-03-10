package ru.ilyasekunov.officeapp.data.repository.user

import ru.ilyasekunov.officeapp.data.datasource.AuthDataSource
import ru.ilyasekunov.officeapp.data.datasource.UserDataSource
import ru.ilyasekunov.officeapp.data.dto.UserDto
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.User

class UserRepositoryImpl(
    private val userDatasource: UserDataSource
) : UserRepository {
    override suspend fun saveChanges(user: UserDto): Result<Unit> {
        return userDatasource.saveChanges(user)
    }

    override suspend fun availableOffices(): Result<List<Office>> {
        return userDatasource.availableOffices()
    }
}