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
import ru.ilyasekunov.officeapp.data.dto.RefreshTokenDto
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

    /** There might be situations when multiple threads or coroutines tend to refresh tokens.
     They all run this method, so to prevent multiple refreshes firstly we take refresh token,
     then synchronized block goes, which guarantee that there will be just one refresh at the same time.
     First thread that will be inside synchronized block performs authentication. Then all others threads that
     are going to perform the same act compare refreshToken that they retrieved before synchronized block with
     a new one, which they are given from token data source. If they differ -> another thread has already made
     a refresh, so we need to perform the request next with new access token. If not, it means that it is the first thread
     that is inside synchronized block and it will be performing a refreshing.
     */
    override fun authenticate(route: Route?, response: Response): Request? {
        if (retryCount(response) >= MAX_ALLOWABLE_RETRY_ATTEMPTS) {
            return null
        }

        // Extract first refreshToken. If null -> return null, there is nothing to refresh.
        val refreshToken = runBlocking { tokenLocalDataSource.token(TokenType.REFRESH) }
            ?: return null

        // First thread performs refreshing
        synchronized(this) {
            // Retrieve refresh token again
            val newRefreshToken = runBlocking { tokenLocalDataSource.token(TokenType.REFRESH) }
                ?: return null

            // If tokens differ -> another thread before already update token.
            // So proceed request with new token
            if (refreshToken != newRefreshToken) {
                val accessToken = runBlocking { tokenLocalDataSource.token(TokenType.ACCESS) }
                    ?: return null
                return retryRequest(response, accessToken)
            }

            // Tokens are the same, so it is the first thread, that should proceed refresh.
            val refreshTokenDto = RefreshTokenDto(refreshToken)
            val refreshResult = runBlocking { authApi.refreshToken(refreshTokenDto) }
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