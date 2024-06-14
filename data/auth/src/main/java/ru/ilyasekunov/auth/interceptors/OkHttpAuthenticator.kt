package ru.ilyasekunov.auth.interceptors

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.ilyasekunov.auth.api.AuthApi
import ru.ilyasekunov.dto.RefreshTokenDto
import ru.ilyasekunov.token.datasource.TokenDataSource
import ru.ilyasekunov.token.datasource.TokenType
import javax.inject.Inject

private const val MAX_ALLOWABLE_RETRY_ATTEMPTS = 1

internal class OkHttpAuthenticator @Inject constructor(
    private val tokenDataSource: TokenDataSource,
    private val authApi: AuthApi
) : Authenticator {
    /*private val authApi = Retrofit.Builder()
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
        .create(AuthApi::class.java)*/

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
        val refreshToken = runBlocking { tokenDataSource.token(TokenType.REFRESH) }
            ?: return null

        // First thread performs refreshing
        synchronized(this) {
            // Retrieve refresh token again
            val newRefreshToken = runBlocking { tokenDataSource.token(TokenType.REFRESH) }
                ?: return null

            // If tokens differ -> another thread before already update token.
            // So proceed request with new token
            if (refreshToken != newRefreshToken) {
                val accessToken = runBlocking { tokenDataSource.token(TokenType.ACCESS) }
                    ?: return null
                return retryRequest(response, accessToken)
            }

            // Tokens are the same, so it is the first thread, that should proceed refresh.
            val refreshTokenDto = RefreshTokenDto(refreshToken)
            val refreshResult = runBlocking { authApi.refreshToken(refreshTokenDto) }
            return if (refreshResult.isSuccessful) {
                val tokens = refreshResult.body()!!
                runBlocking {
                    tokenDataSource.putToken(TokenType.ACCESS, tokens.accessToken)
                    tokenDataSource.putToken(TokenType.REFRESH, tokens.refreshToken)
                }
                retryRequest(response, tokens.accessToken)
            } else null
        }
    }

    private fun retryRequest(
        originalResponse: Response,
        accessToken: String
    ) = originalResponse.request().newBuilder()
        .removeHeader("Authorization")
        .addHeader("Authorization", "Bearer $accessToken")
        .build()

    private fun retryCount(response: Response): Int {
        var retryCount = 0
        var currentResponse = response.priorResponse()
        while (currentResponse != null) {
            currentResponse = currentResponse.priorResponse()
            ++retryCount
        }
        return retryCount
    }
}