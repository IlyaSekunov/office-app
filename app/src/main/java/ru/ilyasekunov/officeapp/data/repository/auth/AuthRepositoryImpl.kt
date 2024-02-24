package ru.ilyasekunov.officeapp.data.repository.auth

import okio.IOException
import retrofit2.HttpException
import ru.ilyasekunov.officeapp.data.ResponseResult
import ru.ilyasekunov.officeapp.data.datasource.local.TokenLocalDatasource
import ru.ilyasekunov.officeapp.data.datasource.remote.AuthRemoteDatasource
import ru.ilyasekunov.officeapp.data.dto.LoginForm
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.User
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDatasource: AuthRemoteDatasource,
    private val tokenDatasource: TokenLocalDatasource
) : AuthRepository {
    override suspend fun register(registrationForm: RegistrationForm): ResponseResult<String> {
        return try {
            val token = authDatasource.register(registrationForm)
            tokenDatasource.putToken(token)
            ResponseResult.success(token)
        } catch (httpException: HttpException) {
            ResponseResult.failure("Cannot send a request to a server")
        } catch (ioException: IOException) {
            ResponseResult.failure("Cannot save a token")
        }
    }

    override suspend fun userInfo(): ResponseResult<User?> {
        return try {
            val user = authDatasource.userInfo()
            updateToken()
            ResponseResult.success(user)
        } catch (httpException: HttpException) {
            ResponseResult.failure("Cannot send a request to a server")
        } catch (ioException: IOException) {
            ResponseResult.failure("Cannot save a token")
        }
    }

    override suspend fun login(loginForm: LoginForm): ResponseResult<String> {
        return try {
            val token = authDatasource.login(loginForm)
            tokenDatasource.putToken(token)
            ResponseResult.success(token)
        } catch (httpException: HttpException) {
            ResponseResult.failure("Cannot send a request to a server")
        } catch (ioException: IOException) {
            ResponseResult.failure("Cannot save a token")
        }
    }

    private suspend fun updateToken() {
        val newToken = authDatasource.updateToken()
        tokenDatasource.putToken(newToken)
    }
}