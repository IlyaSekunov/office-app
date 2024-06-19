package ru.ilyasekunov.author.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ilyasekunov.author.datasource.AuthorDataSource
import ru.ilyasekunov.author.datasource.FakeAuthorDataSource

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FakeAuthorDataSourceModule {
    @Binds
    abstract fun bindAuthorDataSource(
        fakeAuthorDataSource: FakeAuthorDataSource
    ): AuthorDataSource
}