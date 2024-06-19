package ru.ilyasekunov.posts.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ilyasekunov.posts.datasource.FakePostsDataSource
import ru.ilyasekunov.posts.datasource.PostsDataSource

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FakePostsDataSourceModule {
    @Binds
    abstract fun bindPostsDataSource(
        fakePostsDataSource: FakePostsDataSource
    ): PostsDataSource
}