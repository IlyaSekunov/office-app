package ru.ilyasekunov.officeapp.data.datasource

import ru.ilyasekunov.officeapp.data.dto.LoginForm
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.Tokens
import ru.ilyasekunov.officeapp.data.model.User

interface AuthDataSource {
    suspend fun register(registrationForm: RegistrationForm): Result<Tokens>
    suspend fun userInfo(): Result<User>
    suspend fun login(loginForm: LoginForm): Result<Tokens>
    suspend fun refreshToken(refreshToken: String): Result<Tokens>
    suspend fun logout(): Result<Unit>
}