package ru.ilyasekunov.officeapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.ilyasekunov.officeapp.data.api.AuthApi
import ru.ilyasekunov.officeapp.data.api.AuthorApi
import ru.ilyasekunov.officeapp.data.api.ImgurApi
import ru.ilyasekunov.officeapp.data.api.OfficeApi
import ru.ilyasekunov.officeapp.data.api.PostsApi
import ru.ilyasekunov.officeapp.data.api.UserApi
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val IMGUR_URL = "https://api.imgur.com"

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun providePostsApi(retrofit: Retrofit): PostsApi =
        retrofit.create(PostsApi::class.java)

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideOfficeApi(retrofit: Retrofit): OfficeApi =
        retrofit.create(OfficeApi::class.java)

    @Provides
    @Singleton
    fun provideAuthorApi(retrofit: Retrofit): AuthorApi =
        retrofit.create(AuthorApi::class.java)

    @Provides
    @Singleton
    fun provideImgurApi(
        @ImgurTokenInterceptor httpImgurTokenInterceptor: Interceptor
    ): ImgurApi {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addNetworkInterceptor(httpImgurTokenInterceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl(IMGUR_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ImgurApi::class.java)
    }
}