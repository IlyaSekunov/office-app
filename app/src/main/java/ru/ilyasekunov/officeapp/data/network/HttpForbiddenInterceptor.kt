package ru.ilyasekunov.officeapp.data.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import ru.ilyasekunov.officeapp.data.api.AuthApi
import ru.ilyasekunov.officeapp.data.datasource.local.TokenLocalDataSource
import ru.ilyasekunov.officeapp.data.datasource.local.TokenType
import ru.ilyasekunov.officeapp.data.datasource.remote.HttpCodes
import javax.inject.Inject

class HttpForbiddenInterceptor(
    private val tokenLocalDataSource: TokenLocalDataSource
) : Interceptor {
    @Inject
    lateinit var authApi: AuthApi

    override fun intercept(chain: Interceptor.Chain): Response =
        runBlocking {
            val request = chain.request()
            val response = chain.proceed(chain.request())
            if (response.code != HttpCodes.UNAUTHORIZED.code) {
                return@runBlocking response
            }

            // UNAUTHORIZED response
            // If there is no token -> return UNAUTHORIZED
            val refreshToken = tokenLocalDataSource.token(TokenType.REFRESH)
                ?: return@runBlocking Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(HttpCodes.UNAUTHORIZED.code)
                    .message("UNAUTHORIZED")
                    .body(response.body)
                    .build()

            // There is refresh token, try to refresh tokens
            val refreshResult = authApi.refreshToken(refreshToken)
            if (refreshResult.isSuccessful) {
                val tokens = refreshResult.body()!!
                tokenLocalDataSource.putToken(TokenType.REFRESH, tokens.refreshToken)
                tokenLocalDataSource.putToken(TokenType.ACCESS, tokens.accessToken)
                chain.proceed(request)
            } else {
                Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(HttpCodes.UNAUTHORIZED.code)
                    .message("UNAUTHORIZED")
                    .body(refreshResult.errorBody())
                    .build()
            }
        }
}