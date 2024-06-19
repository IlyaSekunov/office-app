package ru.ilyasekunov.user.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ilyasekunov.user.datasource.UserDataSource
import ru.ilyasekunov.user.datasource.UserRemoteDataSource

@Module
@InstallIn(SingletonComponent::class)
internal abstract class UserRemoteDataSourceModule {
    @Binds
    abstract fun bindUserDataSource(
        userRemoteDataSource: UserRemoteDataSource
    ): UserDataSource
}