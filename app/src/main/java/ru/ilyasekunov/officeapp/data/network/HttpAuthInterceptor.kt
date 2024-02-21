package ru.ilyasekunov.officeapp.data.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import ru.ilyasekunov.officeapp.data.datasource.TokenDatasource

class HttpAuthInterceptor(
    private val tokenDatasource: TokenDatasource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val url = request.url.encodedPath
        if (!url.contains("auth") || !url.contains("available-offices")) {
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
        }
        return chain.proceed(request)
    }
}