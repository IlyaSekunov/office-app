package ru.ilyasekunov.auth.datasource

import ru.ilyasekunov.dto.LoginForm
import ru.ilyasekunov.dto.RegistrationForm
import ru.ilyasekunov.model.Tokens
import ru.ilyasekunov.model.User

interface AuthDataSource {
    suspend fun register(registrationForm: RegistrationForm): Result<Tokens>
    suspend fun userInfo(): Result<User>
    suspend fun login(loginForm: LoginForm): Result<Tokens>
    suspend fun refreshToken(refreshToken: String): Result<Tokens>
    suspend fun isEmailValid(email: String): Result<Boolean>
}