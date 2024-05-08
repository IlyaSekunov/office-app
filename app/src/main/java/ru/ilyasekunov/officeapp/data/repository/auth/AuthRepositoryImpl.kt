package ru.ilyasekunov.officeapp.data.repository.auth

import ru.ilyasekunov.officeapp.data.datasource.AuthDataSource
import ru.ilyasekunov.officeapp.data.datasource.local.TokenLocalDataSource
import ru.ilyasekunov.officeapp.data.datasource.local.TokenType
import ru.ilyasekunov.officeapp.data.dto.LoginForm
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.Tokens
import ru.ilyasekunov.officeapp.data.model.User

class AuthRepositoryImpl(
    private val authDatasource: AuthDataSource,
    private val tokenLocalDataSource: TokenLocalDataSource
) : AuthRepository {
    override suspend fun register(registrationForm: RegistrationForm): Result<Tokens> {
        val tokenResult = authDatasource.register(registrationForm)
        return if (tokenResult.isSuccess) {
            val tokens = tokenResult.getOrThrow()
            putTokens(tokens)
            Result.success(tokens)
        } else tokenResult
    }

    override suspend fun userInfo(): Result<User> {
        val userResult = authDatasource.userInfo()
        return if (userResult.isSuccess) {
            refreshTokens()
            val user = userResult.getOrThrow()
            Result.success(user)
        } else userResult
    }

    override suspend fun login(loginForm: LoginForm): Result<Tokens> {
        val loginResult = authDatasource.login(loginForm)
        return if (loginResult.isSuccess) {
            val tokens = loginResult.getOrThrow()
            putTokens(tokens)
            Result.success(tokens)
        } else loginResult
    }

    override suspend fun logout() {
        tokenLocalDataSource.deleteToken(TokenType.ACCESS)
        tokenLocalDataSource.deleteToken(TokenType.REFRESH)
    }

    override suspend fun isEmailValid(email: String): Result<Boolean> {
        return authDatasource.isEmailValid(email)
    }

    private suspend fun putTokens(tokens: Tokens) {
        tokenLocalDataSource.putToken(TokenType.ACCESS, tokens.accessToken)
        tokenLocalDataSource.putToken(TokenType.REFRESH, tokens.refreshToken)
    }

    private suspend fun refreshTokens() {
        val refreshToken = tokenLocalDataSource.token(TokenType.REFRESH)
        refreshToken?.let {
            authDatasource.refreshToken(it).also { result ->
                if (result.isSuccess) {
                    val newTokens = result.getOrThrow()
                    putTokens(newTokens)
                }
            }
        }
    }
}