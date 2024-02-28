package ru.ilyasekunov.officeapp.data.repository.auth

import ru.ilyasekunov.officeapp.data.datasource.AuthDataSource
import ru.ilyasekunov.officeapp.data.datasource.TokenDataSource
import ru.ilyasekunov.officeapp.data.dto.LoginForm
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.User

class AuthRepositoryImpl(
    private val authDatasource: AuthDataSource,
    private val tokenDatasource: TokenDataSource
) : AuthRepository {
    override suspend fun register(registrationForm: RegistrationForm): Result<String> {
        val tokenResult = authDatasource.register(registrationForm)
        return if (tokenResult.isSuccess) {
            val token = tokenResult.getOrThrow()
            tokenDatasource.putToken(token)
            Result.success(token)
        } else tokenResult
    }

    override suspend fun userInfo(): Result<User?> {
        val userResult = authDatasource.userInfo()
        return if (userResult.isSuccess) {
            val user = userResult.getOrThrow()
            updateToken()
            Result.success(user)
        } else userResult
    }

    override suspend fun login(loginForm: LoginForm): Result<String> {
        val loginResult = authDatasource.login(loginForm)
        return if (loginResult.isSuccess) {
            val token = loginResult.getOrThrow()
            tokenDatasource.putToken(token)
            Result.success(token)
        } else loginResult
    }

    private suspend fun updateToken() {
        authDatasource.updateToken().also {
            if (it.isSuccess) {
                val newToken = it.getOrThrow()
                tokenDatasource.putToken(newToken)
            }
        }
    }
}