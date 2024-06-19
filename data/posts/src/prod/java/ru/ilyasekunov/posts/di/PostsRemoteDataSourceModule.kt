package ru.ilyasekunov.posts.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ilyasekunov.posts.datasource.PostsDataSource
import ru.ilyasekunov.posts.datasource.PostsRemoteDataSource

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PostsRemoteDataSourceModule {
    @Binds
    abstract fun bindPostsDataSource(
        postsRemoteDataSource: PostsRemoteDataSource
    ): PostsDataSource
}