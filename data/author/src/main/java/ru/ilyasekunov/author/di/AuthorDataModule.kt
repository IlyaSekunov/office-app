package ru.ilyasekunov.author.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import ru.ilyasekunov.author.api.AuthorApi
import ru.ilyasekunov.author.datasource.AuthorDataSource
import ru.ilyasekunov.author.datasource.AuthorRemoteDataSource
import ru.ilyasekunov.author.repository.AuthorRepository
import ru.ilyasekunov.author.repository.AuthorRepositoryImpl
import ru.ilyasekunov.author.repository.AuthorsPagingRepository
import ru.ilyasekunov.author.repository.AuthorsPagingRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AuthorDataModule {
    @Binds
    abstract fun bindAuthorDataSource(
        authorRemoteDataSource: AuthorRemoteDataSource
    ): AuthorDataSource

    @Binds
    abstract fun bindAuthorRepository(
        authorRepositoryImpl: AuthorRepositoryImpl
    ): AuthorRepository

    @Binds
    abstract fun bindAuthorsPagingRepository(
        authorsPagingRepositoryImpl: AuthorsPagingRepositoryImpl
    ): AuthorsPagingRepository

    companion object {
        @Provides
        @Singleton
        fun provideAuthorApi(retrofit: Retrofit): AuthorApi = retrofit.create()
    }
}