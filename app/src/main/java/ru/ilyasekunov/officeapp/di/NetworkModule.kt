package ru.ilyasekunov.officeapp.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.ilyasekunov.officeapp.data.datasource.local.TokenLocalDataSource
import ru.ilyasekunov.officeapp.data.network.HttpAccessTokenInterceptor
import ru.ilyasekunov.officeapp.data.network.HttpForbiddenInterceptor
import ru.ilyasekunov.officeapp.data.network.HttpImgurTokenInterceptor
import ru.ilyasekunov.officeapp.data.network.OkHttpAuthenticator
import ru.ilyasekunov.officeapp.data.serialize.JsonLocalDateTimeDeserializer
import java.time.LocalDateTime
import javax.inject.Qualifier
import javax.inject.Singleton

const val BASE_URl = "http://10.0.2.2:8189/api/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    @AccessTokenInterceptor
    fun provideAccessTokenInterceptor(
        @LocalDataSource tokenDatasource: TokenLocalDataSource
    ): Interceptor = HttpAccessTokenInterceptor(tokenDatasource)

    @Provides
    @Singleton
    @ForbiddenInterceptor
    fun provideForbiddenInterceptor(
        @LocalDataSource tokenLocalDataSource: TokenLocalDataSource
    ): Interceptor = HttpForbiddenInterceptor(tokenLocalDataSource)

    @Provides
    @Singleton
    @ImgurTokenInterceptor
    fun provideHttpImgurTokenInterceptor(): Interceptor = HttpImgurTokenInterceptor()

    @Provides
    @Singleton
    fun provideOkHttpAuthenticator(
        @LocalDataSource tokenLocalDataSource: TokenLocalDataSource
    ): Authenticator = OkHttpAuthenticator(tokenLocalDataSource)

    @Provides
    @Singleton
    fun provideRetrofit(
        @AccessTokenInterceptor accessTokenInterceptor: Interceptor,
        authenticator: Authenticator
    ): Retrofit {
        val httpClient = OkHttpClient.Builder()
            .authenticator(authenticator)
            .addNetworkInterceptor(accessTokenInterceptor)
            .build()
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime::class.java, JsonLocalDateTimeDeserializer())
            .create()
        return Retrofit.Builder()
            .baseUrl(BASE_URl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AccessTokenInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ForbiddenInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ImgurTokenInterceptor