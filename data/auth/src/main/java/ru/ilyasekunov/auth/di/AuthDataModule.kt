package ru.ilyasekunov.auth.di

import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.ilyasekunov.auth.api.AuthApi
import ru.ilyasekunov.auth.interceptors.HttpAccessTokenInterceptor
import ru.ilyasekunov.auth.interceptors.OkHttpAuthenticator
import ru.ilyasekunov.auth.repository.AuthRepository
import ru.ilyasekunov.auth.repository.AuthRepositoryImpl
import ru.ilyasekunov.network.di.BASE_URl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AuthDataModule {
    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    companion object {
        @Provides
        fun provideAuthApi(
            retrofit: Retrofit,
        ): AuthApi = retrofit.create()

        @Provides
        @Singleton
        fun provideRetrofit(
            gson: Gson,
            authenticator: OkHttpAuthenticator,
            httpAccessTokenInterceptor: HttpAccessTokenInterceptor
        ): Retrofit {
            val okHttpClient = OkHttpClient()
                .newBuilder()
                .authenticator(authenticator)
                .addNetworkInterceptor(httpAccessTokenInterceptor)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
    }
}