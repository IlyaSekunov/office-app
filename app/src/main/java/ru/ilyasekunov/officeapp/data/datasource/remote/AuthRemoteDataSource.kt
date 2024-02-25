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
    override suspend fun register(registrationForm: RegistrationForm): String =
        withContext(ioDispatcher) {
            authApi.register(registrationForm)
        }

    override suspend fun userInfo(): User =
        withContext(ioDispatcher) {
            authApi.userInfo()
        }

    override suspend fun login(loginForm: LoginForm): String =
        withContext(ioDispatcher) {
            authApi.login(loginForm)
        }

    override suspend fun updateToken(): String =
        withContext(ioDispatcher) {
            authApi.updateToken()
        }
}