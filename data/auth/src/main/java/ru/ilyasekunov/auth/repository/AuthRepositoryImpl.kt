package ru.ilyasekunov.auth.repository

import ru.ilyasekunov.auth.datasource.AuthDataSource
import ru.ilyasekunov.dto.LoginForm
import ru.ilyasekunov.dto.RegistrationForm
import ru.ilyasekunov.model.Tokens
import ru.ilyasekunov.model.User
import ru.ilyasekunov.token.datasource.TokenDataSource
import ru.ilyasekunov.token.datasource.TokenType
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authDatasource: AuthDataSource,
    private val tokenDataSource: TokenDataSource
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
        tokenDataSource.deleteToken(TokenType.ACCESS)
        tokenDataSource.deleteToken(TokenType.REFRESH)
    }

    override suspend fun isEmailValid(email: String): Result<Boolean> {
        return authDatasource.isEmailValid(email)
    }

    private suspend fun putTokens(tokens: Tokens) {
        tokenDataSource.putToken(TokenType.ACCESS, tokens.accessToken)
        tokenDataSource.putToken(TokenType.REFRESH, tokens.refreshToken)
    }
}