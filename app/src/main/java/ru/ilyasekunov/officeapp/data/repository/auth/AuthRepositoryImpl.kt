package ru.ilyasekunov.officeapp.data.repository.auth

import ru.ilyasekunov.officeapp.data.datasource.TokenDatasource
import ru.ilyasekunov.officeapp.data.datasource.AuthDatasource
import ru.ilyasekunov.officeapp.data.dto.LoginForm
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.User
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDatasource: AuthDatasource,
    private val tokenDatasource: TokenDatasource
) : AuthRepository {
    override suspend fun register(registrationForm: RegistrationForm): String {
        val token = authDatasource.register(registrationForm)
        tokenDatasource.putToken(token)
        return token
    }

    override suspend fun userInfo(): User? {
        val token = tokenDatasource.token()
        return token?.let {
            authDatasource.userInfo(it)
        }
    }

    override suspend fun login(loginForm: LoginForm): String {
        val token = authDatasource.login(loginForm)
        tokenDatasource.putToken(token)
        return token
    }
}