package ru.ilyasekunov.auth.datasource

import kotlinx.coroutines.delay
import ru.ilyasekunov.dto.LoginForm
import ru.ilyasekunov.dto.RegistrationForm
import ru.ilyasekunov.model.Tokens
import ru.ilyasekunov.model.User
import ru.ilyasekunov.test.user
import javax.inject.Inject

class FakeAuthDataSource @Inject constructor(): AuthDataSource {
    override suspend fun register(registrationForm: RegistrationForm): Result<Tokens> {
        TODO("Not yet implemented")
    }

    override suspend fun userInfo(): Result<User> {
        delay(1200L)
        return if (user != null) {
            Result.success(user!!)
        } else {
            Result.failure(Exception())
        }
    }


    override suspend fun login(loginForm: LoginForm): Result<Tokens> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshToken(refreshToken: String): Result<Tokens> {
        TODO("Not yet implemented")
    }

    override suspend fun isEmailValid(email: String): Result<Boolean> {
        delay(1200L)
        return Result.success(true)
    }
}