package ru.ilyasekunov.auth.repository

import ru.ilyasekunov.dto.LoginForm
import ru.ilyasekunov.dto.RegistrationForm
import ru.ilyasekunov.model.Tokens
import ru.ilyasekunov.model.User

interface AuthRepository {
    suspend fun register(registrationForm: RegistrationForm): Result<Tokens>
    suspend fun userInfo(): Result<User>
    suspend fun login(loginForm: LoginForm): Result<Tokens>
    suspend fun logout()
    suspend fun isEmailValid(email: String): Result<Boolean>
}