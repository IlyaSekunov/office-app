package ru.ilyasekunov.auth.interceptors

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import ru.ilyasekunov.token.datasource.TokenDataSource
import ru.ilyasekunov.token.datasource.TokenType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class HttpAccessTokenInterceptor @Inject constructor(
    private val tokenDatasource: TokenDataSource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val url = request.url().encodedPath()

        if (
            url.contains("register") ||
            url.contains("login") ||
            url.contains("refresh-token") ||
            url.contains("email-valid") ||
            url.contains("offices")
        ) {
            return chain.proceed(request)
        }

        // Otherwise, attach auth token to request
        val token = runBlocking { tokenDatasource.token(TokenType.ACCESS) }
        token?.let {
            val tokenHeader = "Bearer $it"
            request = request.newBuilder()
                .addHeader(
                    "Authorization",
                    tokenHeader
                )
                .build()
        }
        return chain.proceed(request)
    }
}