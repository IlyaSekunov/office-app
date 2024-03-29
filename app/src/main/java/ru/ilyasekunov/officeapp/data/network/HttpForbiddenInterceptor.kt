package ru.ilyasekunov.officeapp.data.network

import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.ilyasekunov.officeapp.data.api.AuthApi
import ru.ilyasekunov.officeapp.data.datasource.local.TokenLocalDataSource
import ru.ilyasekunov.officeapp.data.datasource.local.TokenType
import ru.ilyasekunov.officeapp.data.datasource.remote.HttpCodes
import ru.ilyasekunov.officeapp.data.dto.RefreshTokenDto
import ru.ilyasekunov.officeapp.data.serialize.JsonLocalDateTimeDeserializer
import ru.ilyasekunov.officeapp.di.BASE_URl
import java.time.LocalDateTime

class HttpForbiddenInterceptor(
    private val tokenLocalDataSource: TokenLocalDataSource
) : Interceptor {
    private val authApi = Retrofit.Builder()
        .baseUrl(BASE_URl)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(LocalDateTime::class.java, JsonLocalDateTimeDeserializer())
                    .create()
            )
        )
        .build()
        .create(AuthApi::class.java)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code != HttpCodes.UNAUTHORIZED) {
            return response
        }

        // UNAUTHORIZED response
        // If there is no token -> return UNAUTHORIZED
        val refreshToken = runBlocking { tokenLocalDataSource.token(TokenType.REFRESH) }
            ?: return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(HttpCodes.UNAUTHORIZED)
                .message("UNAUTHORIZED")
                .body(response.body)
                .build()

        // There is refresh token, try to refresh tokens
        val refreshTokenDto = RefreshTokenDto(refreshToken)
        val refreshResult = runBlocking { authApi.refreshToken(refreshTokenDto) }
        if (refreshResult.isSuccessful) {
            val tokens = refreshResult.body()!!
            runBlocking {
                tokenLocalDataSource.putToken(TokenType.REFRESH, tokens.refreshToken)
                tokenLocalDataSource.putToken(TokenType.ACCESS, tokens.accessToken)
            }
            return chain.proceed(request)
        } else {
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(HttpCodes.UNAUTHORIZED)
                .message("UNAUTHORIZED")
                .body(refreshResult.errorBody())
                .build()
        }
    }
}