package ru.ilyasekunov.officeapp.data.repository.auth

import ru.ilyasekunov.officeapp.data.datasource.AuthDatasource
import ru.ilyasekunov.officeapp.data.datasource.TokenDatasource
import ru.ilyasekunov.officeapp.data.dto.LoginForm
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.User
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDatasource: AuthDatasource,
    private val tokenDatasource: TokenDatasource
) : AuthRepository {
    override suspend fun register(registrationForm: RegistrationForm): Result<String> {
        return try {
            val token = authDatasource.register(registrationForm)
            tokenDatasource.putToken(token)
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun userInfo(): Result<User?> {
        return try {
            val token = tokenDatasource.token()
            Result.success(
                token?.let { authDatasource.userInfo(it) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(loginForm: LoginForm): Result<String> {
        return try {
            val token = authDatasource.login(loginForm)
            tokenDatasource.putToken(token)
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}