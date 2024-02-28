package ru.ilyasekunov.officeapp.data.datasource.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
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
            authApi.register(registrationForm).run {
                if (isSuccessful) {
                    Result.success(body()!!)
                } else {
                    Result.failure(HttpException(this))
                }
            }
        }

    override suspend fun userInfo(): Result<User> =
        withContext(ioDispatcher) {
            authApi.userInfo().run {
                if (isSuccessful) {
                    Result.success(body()!!)
                } else {
                    Result.failure(HttpException(this))
                }
            }
        }

    override suspend fun login(loginForm: LoginForm): Result<String> =
        withContext(ioDispatcher) {
            authApi.login(loginForm).run {
                if (isSuccessful) {
                    Result.success(body()!!)
                } else {
                    Result.failure(HttpException(this))
                }
            }
        }

    override suspend fun updateToken(): Result<String> =
        withContext(ioDispatcher) {
            authApi.updateToken().run {
                if (isSuccessful) {
                    Result.success(body()!!)
                } else {
                    Result.failure(HttpException(this))
                }
            }
        }
}