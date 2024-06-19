package ru.ilyasekunov.author.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ilyasekunov.author.datasource.AuthorDataSource
import ru.ilyasekunov.author.datasource.AuthorRemoteDataSource

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AuthorRemoteDataSourceModule {
    @Binds
    abstract fun bindAuthorDataSource(
        authorRemoteDataSource: AuthorRemoteDataSource
    ): AuthorDataSource
}