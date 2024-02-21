package ru.ilyasekunov.officeapp.data.datasource.local.mock.auth

import ru.ilyasekunov.officeapp.data.datasource.AuthDatasource
import ru.ilyasekunov.officeapp.data.dto.LoginForm
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.User

class MockAuthDatasourceImpl : AuthDatasource {
    override suspend fun register(registrationForm: RegistrationForm): String {
        TODO("Not yet implemented")
    }

    override suspend fun userInfo(token: String): User {
        TODO("Not yet implemented")
    }

    override suspend fun login(loginForm: LoginForm): String {
        TODO("Not yet implemented")
    }
}