package ru.ilyasekunov.officeapp.data.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import ru.ilyasekunov.officeapp.data.api.AuthApi
import ru.ilyasekunov.officeapp.data.datasource.local.TokenLocalDataSource
import ru.ilyasekunov.officeapp.data.datasource.local.TokenType
import javax.inject.Inject

class HttpForbiddenInterceptor(
    private val tokenLocalDataSource: TokenLocalDataSource
) : Interceptor {
    @Inject
    lateinit var authApi: AuthApi

    override fun intercept(chain: Interceptor.Chain): Response =
        runBlocking {
            val response = chain.proceed(chain.request())
            if (response.code != 401) {
                return@runBlocking response
            }

            // 401 response
            // If there is no token -> return 401
            val refreshToken = tokenLocalDataSource.token(TokenType.REFRESH)
                ?: return@runBlocking Response.Builder()
                    .code(401)
                    .build()

            // There is refresh token, try to refresh tokens
            val refreshResult = authApi.refreshToken(refreshToken)
            if (refreshResult.isSuccessful) {
                val tokens = refreshResult.body()!!
                tokenLocalDataSource.putToken(TokenType.REFRESH, tokens.refreshToken)
                tokenLocalDataSource.putToken(TokenType.ACCESS, tokens.accessToken)
                chain.proceed(chain.request())
            } else {
                Response.Builder()
                    .code(401)
                    .build()
            }
        }
}