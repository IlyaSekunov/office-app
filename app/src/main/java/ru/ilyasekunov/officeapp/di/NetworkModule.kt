package ru.ilyasekunov.officeapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.ilyasekunov.officeapp.data.datasource.TokenDatasource
import ru.ilyasekunov.officeapp.data.network.HttpAuthInterceptor
import javax.inject.Singleton

private const val BASE_URl = "http://10.0.2.2:8080/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(tokenDatasource: TokenDatasource): Retrofit {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(HttpAuthInterceptor(tokenDatasource))
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}