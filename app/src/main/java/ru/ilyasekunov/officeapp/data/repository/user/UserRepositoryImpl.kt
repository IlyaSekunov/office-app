package ru.ilyasekunov.officeapp.data.repository.user

import ru.ilyasekunov.officeapp.data.datasource.UserDatasource
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDatasource: UserDatasource
) : UserRepository {
    override suspend fun user(): User? {
        return userDatasource.user()
    }

    override suspend fun register(registrationForm: RegistrationForm) {
        userDatasource.register(registrationForm)
    }

    override suspend fun saveChanges(user: User) {
        userDatasource.saveChanges(user)
    }
}