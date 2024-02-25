package ru.ilyasekunov.officeapp.data.datasource

import ru.ilyasekunov.officeapp.data.dto.LoginForm
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.User

interface AuthDataSource {
    suspend fun register(registrationForm: RegistrationForm): String
    suspend fun userInfo(): User
    suspend fun login(loginForm: LoginForm): String
    suspend fun updateToken(): String
}