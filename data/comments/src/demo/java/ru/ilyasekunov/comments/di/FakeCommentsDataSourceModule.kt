package ru.ilyasekunov.comments.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ilyasekunov.comments.datasource.CommentsDataSource
import ru.ilyasekunov.comments.datasource.FakeCommentsDataSource

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FakeCommentsDataSourceModule {
    @Binds
    abstract fun bindCommentsDataSource(
        fakeCommentsDataSource: FakeCommentsDataSource
    ): CommentsDataSource
}