package ru.ilyasekunov.officeapp.data.repository.user

import ru.ilyasekunov.officeapp.data.datasource.UserDataSource
import ru.ilyasekunov.officeapp.data.dto.UserDto
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.User

class UserRepositoryImpl(
    private val userDatasource: UserDataSource
) : UserRepository {
    override suspend fun user(): User? {
        return userDatasource.user()
    }

    override suspend fun saveChanges(user: UserDto) {
        userDatasource.saveChanges(user)
    }

    override suspend fun availableOffices(): List<Office> {
        return userDatasource.availableOffices()
    }
}