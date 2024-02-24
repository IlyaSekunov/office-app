package ru.ilyasekunov.officeapp.data.datasource.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.ilyasekunov.officeapp.data.api.AuthApi
import ru.ilyasekunov.officeapp.data.dto.LoginForm
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRemoteDatasource @Inject constructor(
    private val authApi: AuthApi,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun register(registrationForm: RegistrationForm): String =
        withContext(ioDispatcher) {
            authApi.register(registrationForm)
        }

    suspend fun userInfo(): User =
        withContext(ioDispatcher) {
            authApi.userInfo()
        }

    suspend fun login(loginForm: LoginForm): String =
        withContext(ioDispatcher) {
            authApi.login(loginForm)
        }

    suspend fun updateToken(): String =
        withContext(ioDispatcher) {
            authApi.updateToken()
        }
}