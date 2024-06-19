package ru.ilyasekunov.auth.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ilyasekunov.auth.datasource.AuthDataSource
import ru.ilyasekunov.auth.datasource.FakeAuthDataSource

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FakeAuthDataSourceModule {
    @Binds
    abstract fun bindAuthDataSource(
        fakeAuthDataSource: FakeAuthDataSource
    ): AuthDataSource
}