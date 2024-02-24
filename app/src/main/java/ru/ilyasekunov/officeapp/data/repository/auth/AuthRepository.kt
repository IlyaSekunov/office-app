package ru.ilyasekunov.officeapp.data.repository.auth

import ru.ilyasekunov.officeapp.data.ResponseResult
import ru.ilyasekunov.officeapp.data.dto.LoginForm
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.User

interface AuthRepository {
    suspend fun register(registrationForm: RegistrationForm): ResponseResult<String>
    suspend fun userInfo(): ResponseResult<User?>
    suspend fun login(loginForm: LoginForm): ResponseResult<String>
}