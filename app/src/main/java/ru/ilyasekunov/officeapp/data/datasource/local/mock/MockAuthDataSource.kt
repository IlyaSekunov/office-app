package ru.ilyasekunov.officeapp.data.datasource.local.mock

import kotlinx.coroutines.delay
import ru.ilyasekunov.officeapp.data.datasource.AuthDataSource
import ru.ilyasekunov.officeapp.data.dto.LoginForm
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.Tokens
import ru.ilyasekunov.officeapp.data.model.User

class MockAuthDataSource : AuthDataSource {
    override suspend fun register(registrationForm: RegistrationForm): Result<Tokens> {
        TODO("Not yet implemented")
    }

    override suspend fun userInfo(): Result<User> =
        if (User != null) Result.success(User!!) else Result.failure(Exception())

    override suspend fun login(loginForm: LoginForm): Result<Tokens> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshToken(refreshToken: String): Result<Tokens> {
        TODO("Not yet implemented")
    }

    override suspend fun isEmailValid(email: String): Result<Boolean> {
        delay(3000L)
        return Result.success(true)
    }
}