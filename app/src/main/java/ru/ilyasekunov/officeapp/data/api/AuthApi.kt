package ru.ilyasekunov.officeapp.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.ilyasekunov.officeapp.data.dto.LoginForm
import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.Tokens
import ru.ilyasekunov.officeapp.data.model.User

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body registrationForm: RegistrationForm): Response<Tokens>
    @GET("auth/user-info")
    suspend fun userInfo(): Response<User>
    @POST("auth/login")
    suspend fun login(@Body loginForm: LoginForm): Response<Tokens>
    @POST("auth/refresh-token")
    suspend fun refreshToken(@Body refreshToken: String): Response<Tokens>
    @GET("auth/email-valid")
    suspend fun isEmailValid(@Query("email") email: String): Response<Unit>
}