package ru.ilyasekunov.officeapp.data.network

import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.ilyasekunov.officeapp.data.api.AuthApi
import ru.ilyasekunov.officeapp.data.datasource.local.TokenLocalDataSource
import ru.ilyasekunov.officeapp.data.datasource.local.TokenType
import ru.ilyasekunov.officeapp.data.serialize.JsonLocalDateTimeDeserializer
import ru.ilyasekunov.officeapp.di.BASE_URl
import java.time.LocalDateTime

private const val MAX_ALLOWABLE_RETRY_ATTEMPTS = 1

class OkHttpAuthenticator(
    private val tokenLocalDataSource: TokenLocalDataSource
) : Authenticator {
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

    override fun authenticate(route: Route?, response: Response): Request? {
        if (retryCount(response) >= MAX_ALLOWABLE_RETRY_ATTEMPTS) {
            return null
        }

        synchronized(this) {
            val refreshToken = runBlocking { tokenLocalDataSource.token(TokenType.REFRESH) }
                ?: return null
            val refreshResult = runBlocking { authApi.refreshToken(refreshToken) }
            return if (refreshResult.isSuccessful) {
                val tokens = refreshResult.body()!!
                runBlocking {
                    tokenLocalDataSource.putToken(TokenType.ACCESS, tokens.accessToken)
                    tokenLocalDataSource.putToken(TokenType.REFRESH, tokens.refreshToken)
                }
                retryRequest(response, tokens.accessToken)
            } else null
        }
    }

    private fun retryRequest(
        originalResponse: Response,
        accessToken: String
    ) = originalResponse.request.newBuilder()
            .removeHeader("Authorization")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

    private fun retryCount(response: Response): Int {
        var retryCount = 0
        var currentResponse = response.priorResponse
        while (currentResponse != null) {
            currentResponse = currentResponse.priorResponse
            ++retryCount
        }
        return retryCount
    }
}