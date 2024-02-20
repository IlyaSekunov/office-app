package ru.ilyasekunov.officeapp.data.datasource.local.mock.user

import ru.ilyasekunov.officeapp.data.datasource.UserDatasource
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.User
import ru.ilyasekunov.officeapp.data.officeList
import ru.ilyasekunov.officeapp.preview.userInfoPreview

class MockUserDatasource : UserDatasource {
    private var user: User? = userInfoPreview

    override suspend fun user(): User? = user

    override suspend fun register(registrationForm: RegistrationForm) {
        user = registrationForm.toUser()
    }

    override suspend fun saveChanges(user: User) {
        this.user = user
    }

    private fun RegistrationForm.toUser(): User =
        User(
            id = 0,
            email = email,
            password = password,
            name = userInfo.name,
            surname = userInfo.surname,
            job = userInfo.job,
            photo = userInfo.photo,
            office = officeList[userInfo.officeId]
        )
}