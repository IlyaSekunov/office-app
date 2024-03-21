package ru.ilyasekunov.officeapp.data.datasource.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.ilyasekunov.officeapp.data.api.AuthApi
import ru.ilyasekunov.officeapp.data.datasource.AuthDataSource
import ru.ilyasekunov.officeapp.data.dto.LoginForm
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.Tokens
import ru.ilyasekunov.officeapp.data.model.User

class AuthRemoteDataSource(
    private val authApi: AuthApi,
    private val ioDispatcher: CoroutineDispatcher
) : AuthDataSource {
    override suspend fun register(registrationForm: RegistrationForm): Result<Tokens> =
        withContext(ioDispatcher) {
            handleResponse { authApi.register(registrationForm) }
        }

    override suspend fun userInfo(): Result<User> =
        withContext(ioDispatcher) {
            handleResponse { authApi.userInfo() }
        }

    override suspend fun login(loginForm: LoginForm): Result<Tokens> =
        withContext(ioDispatcher) {
            handleResponse { authApi.login(loginForm) }
        }

    override suspend fun refreshToken(refreshToken: String): Result<Tokens> =
        withContext(ioDispatcher) {
            handleResponse { authApi.refreshToken(refreshToken) }
        }

    override suspend fun isEmailValid(email: String): Result<Boolean> =
        withContext(ioDispatcher) {
            handleIsEmailValidResponse { authApi.isEmailValid(email) }
        }
}