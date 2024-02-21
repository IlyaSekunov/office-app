package ru.ilyasekunov.officeapp.data.repository.user

import ru.ilyasekunov.officeapp.data.datasource.UserDatasource
import ru.ilyasekunov.officeapp.data.dto.UserDto
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDatasource: UserDatasource
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