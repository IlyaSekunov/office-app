package ru.ilyasekunov.officeapp.data.datasource.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.ilyasekunov.officeapp.data.api.AuthApi
import ru.ilyasekunov.officeapp.data.datasource.AuthDataSource
import ru.ilyasekunov.officeapp.data.dto.LoginForm
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.User

class AuthRemoteDataSource(
    private val authApi: AuthApi,
    private val ioDispatcher: CoroutineDispatcher
) : AuthDataSource {
    override suspend fun register(registrationForm: RegistrationForm): Result<String> =
        withContext(ioDispatcher) {
            handleResponse { authApi.register(registrationForm) }
        }

    override suspend fun userInfo(): Result<User> =
        withContext(ioDispatcher) {
            handleResponse { authApi.userInfo() }
        }

    override suspend fun login(loginForm: LoginForm): Result<String> =
        withContext(ioDispatcher) {
            handleResponse { authApi.login(loginForm) }
        }

    override suspend fun updateToken(): Result<String> =
        withContext(ioDispatcher) {
            handleResponse { authApi.updateToken() }
        }

    override suspend fun logout(): Result<Unit> =
        withContext(ioDispatcher) {
            handleResponse { authApi.logout() }
        }
}