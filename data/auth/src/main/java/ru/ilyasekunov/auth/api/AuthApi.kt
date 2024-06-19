package ru.ilyasekunov.auth.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.ilyasekunov.dto.LoginForm
import ru.ilyasekunov.dto.RefreshTokenDto
import ru.ilyasekunov.dto.RegistrationForm
import ru.ilyasekunov.model.Tokens
import ru.ilyasekunov.model.User

internal interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body registrationForm: RegistrationForm): Response<Tokens>
    @GET("auth/user-info")
    suspend fun userInfo(): Response<User>
    @POST("auth/login")
    suspend fun login(@Body loginForm: LoginForm): Response<Tokens>
    @POST("auth/refresh-token")
    suspend fun refreshToken(@Body refreshTokenDto: RefreshTokenDto): Response<Tokens>
    @GET("auth/email-valid")
    suspend fun isEmailValid(@Query("email") email: String): Response<Unit>
}