package ru.ilyasekunov.user.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ilyasekunov.user.datasource.FakeUserDataSource
import ru.ilyasekunov.user.datasource.UserDataSource

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FakeUserDataSourceModule {
    @Binds
    abstract fun bindUserDataSource(
        fakeUserDataSource: FakeUserDataSource
    ): UserDataSource
}