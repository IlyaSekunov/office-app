package ru.ilyasekunov.officeapp.data.repository.auth

import ru.ilyasekunov.officeapp.data.dto.LoginForm
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.User

interface AuthRepository {
    suspend fun register(registrationForm: RegistrationForm): Result<String>
    suspend fun userInfo(): Result<User?>
    suspend fun login(loginForm: LoginForm): Result<String>
    suspend fun logout(): Result<Unit>
}