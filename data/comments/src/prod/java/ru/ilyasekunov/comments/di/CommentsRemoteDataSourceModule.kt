package ru.ilyasekunov.comments.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ilyasekunov.comments.datasource.CommentsDataSource
import ru.ilyasekunov.comments.datasource.CommentsRemoteDataSource

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CommentsRemoteDataSourceModule {
    @Binds
    abstract fun bindCommentsDataSource(
        commentsRemoteDataSource: CommentsRemoteDataSource
    ): CommentsDataSource
}