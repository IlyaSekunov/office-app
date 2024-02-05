package ru.ilyasekunov.officeapp.data.repository.user

import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.User
import ru.ilyasekunov.officeapp.data.officeList
import ru.ilyasekunov.officeapp.preview.userInfoPreview

class UserRepositoryImpl : UserRepository {
    override suspend fun findUser(): User? {
        return userInfoPreview
    }

    override suspend fun findOfficeList(): List<Office> {
        return officeList
    }

    override suspend fun register(registrationForm: RegistrationForm) {

    }
}