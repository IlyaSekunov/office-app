package ru.ilyasekunov.officeapp.data.network

import okhttp3.Interceptor
import okhttp3.Response
import ru.ilyasekunov.officeapp.data.secret.ImgurApiKeys

class HttpImgurTokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val clientIdToken = "Client-ID ${ImgurApiKeys.CLIENT_ID}"
        val request = chain
            .request()
            .newBuilder()
            .addHeader(name = "Authorization", value = clientIdToken)
            .build()
        return chain.proceed(request)
    }
}