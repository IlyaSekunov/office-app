package ru.ilyasekunov.officeapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import ru.ilyasekunov.officeapp.data.api.AuthApi
import ru.ilyasekunov.officeapp.data.api.PostsApi
import javax.inject.Singleton

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
}