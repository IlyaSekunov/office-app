package ru.ilyasekunov.officeapp.data.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import ru.ilyasekunov.officeapp.data.datasource.local.TokenLocalDatasource

class HttpAuthInterceptor(
    private val tokenDatasource: TokenLocalDatasource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val url = request.url.encodedPath

        // Don't attach auth token on registration and login
        if (url.contains("register") || url.contains("login")) {
            return chain.proceed(request)
        }

        // Otherwise, attach auth token to request
        val token = runBlocking { tokenDatasource.token() }
        token?.let {
            val tokenHeader = "Bearer $it"
            request = request.newBuilder()
                .addHeader(
                    name = "Authorization",
                    value = tokenHeader
                )
                .build()
        }
        return chain.proceed(request)
    }
}