package ru.ilyasekunov.auth.datasource

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.ilyasekunov.auth.api.AuthApi
import ru.ilyasekunov.common.di.IoDispatcher
import ru.ilyasekunov.dto.LoginForm
import ru.ilyasekunov.dto.RefreshTokenDto
import ru.ilyasekunov.dto.RegistrationForm
import ru.ilyasekunov.model.Tokens
import ru.ilyasekunov.model.User
import ru.ilyasekunov.network.handleIsEmailValidResponse
import ru.ilyasekunov.network.handleLoginResponse
import ru.ilyasekunov.network.handleResponse
import javax.inject.Inject

internal class AuthRemoteDataSource @Inject constructor(
    private val authApi: AuthApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
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
            handleLoginResponse { authApi.login(loginForm) }
        }

    override suspend fun refreshToken(refreshToken: String): Result<Tokens> =
        withContext(ioDispatcher) {
            val refreshTokenDto = RefreshTokenDto(refreshToken)
            handleResponse { authApi.refreshToken(refreshTokenDto) }
        }

    override suspend fun isEmailValid(email: String): Result<Boolean> =
        withContext(ioDispatcher) {
            handleIsEmailValidResponse { authApi.isEmailValid(email) }
        }
}