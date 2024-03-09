package ru.ilyasekunov.officeapp.data.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import ru.ilyasekunov.officeapp.data.datasource.AuthDataSource
import ru.ilyasekunov.officeapp.data.datasource.local.TokenLocalDataSource
import ru.ilyasekunov.officeapp.data.datasource.local.TokenType

class HttpForbiddenInterceptor(
    private val authDataSource: AuthDataSource,
    private val tokenLocalDataSource: TokenLocalDataSource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        runBlocking {
            val response = chain.proceed(chain.request())
            if (response.code != 403) {
                return@runBlocking response
            }

            // 403 response
            // If there is no token -> return 403
            val refreshToken = tokenLocalDataSource.token(TokenType.REFRESH)
                ?: return@runBlocking Response.Builder()
                    .code(403)
                    .build()

            // There is refresh token, try to refresh tokens
            val refreshResult = authDataSource.refreshToken(refreshToken)
            if (refreshResult.isSuccess) {
                val tokens = refreshResult.getOrThrow()
                tokenLocalDataSource.putToken(TokenType.REFRESH, tokens.refreshToken)
                tokenLocalDataSource.putToken(TokenType.ACCESS, tokens.accessToken)
                chain.proceed(chain.request())
            } else {
                Response.Builder()
                    .code(403)
                    .build()
            }
        }
}