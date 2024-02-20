package ru.ilyasekunov.officeapp.data.datasource

import retrofit2.http.GET
import retrofit2.http.POST
import ru.ilyasekunov.officeapp.data.dto.LoginForm
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.User

interface AuthDatasource {
    @POST("auth/register")
    suspend fun register(registrationForm: RegistrationForm): String
    @GET("auth/user-info")
    suspend fun userInfo(token: String): User
    @GET("auth/login")
    suspend fun login(loginForm: LoginForm): String
}