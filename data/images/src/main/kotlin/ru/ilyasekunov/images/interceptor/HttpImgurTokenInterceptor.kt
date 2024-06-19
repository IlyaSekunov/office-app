package ru.ilyasekunov.images.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import ru.ilyasekunov.officeapp.data.images.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HttpImgurTokenInterceptor @Inject constructor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val clientIdToken = "Client-ID ${BuildConfig.IMGUR_CLIENT_ID}"
        val request = chain
            .request()
            .newBuilder()
            .addHeader("Authorization", clientIdToken)
            .build()
        return chain.proceed(request)
    }
}