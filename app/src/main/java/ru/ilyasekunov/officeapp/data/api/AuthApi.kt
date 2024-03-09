package ru.ilyasekunov.officeapp.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ru.ilyasekunov.officeapp.data.dto.LoginForm
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.Tokens
import ru.ilyasekunov.officeapp.data.model.User

interface AuthApi {
    @POST("auth/register")
    suspend fun register(registrationForm: RegistrationForm): Response<Tokens>
    @GET("auth/user-info")
    suspend fun userInfo(): Response<User>
    @GET("auth/login")
    suspend fun login(loginForm: LoginForm): Response<Tokens>
    @GET("auth/refresh-token")
    suspend fun refreshToken(@Body refreshToken: String): Response<Tokens>
    @POST("auth/logout")
    suspend fun logout(): Response<Unit>
}