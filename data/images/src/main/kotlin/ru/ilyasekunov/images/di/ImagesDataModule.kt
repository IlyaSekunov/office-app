package ru.ilyasekunov.images.di

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
import ru.ilyasekunov.images.api.ImgurApi
import ru.ilyasekunov.images.datasource.ImagesUploaderDataSource
import ru.ilyasekunov.images.datasource.ImgurRemoteDataSource
import ru.ilyasekunov.images.interceptor.HttpImgurTokenInterceptor
import ru.ilyasekunov.images.repository.ImagesRepository
import ru.ilyasekunov.images.repository.ImagesRepositoryImpl
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val IMGUR_URL = "https://api.imgur.com"

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ImagesDataModule {
    @Binds
    abstract fun bindImagesUploaderDataSource(
        imgurRemoteDataSource: ImgurRemoteDataSource
    ): ImagesUploaderDataSource

    @Binds
    abstract fun bindImagesRepository(
        imagesRepositoryImpl: ImagesRepositoryImpl
    ): ImagesRepository

    companion object {
        @Provides
        @Singleton
        fun provideImgurApi(
            gson: Gson,
            httpImgurTokenInterceptor: HttpImgurTokenInterceptor
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
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create()
        }
    }
}