package ru.ilyasekunov.posts.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import ru.ilyasekunov.posts.api.PostsApi
import ru.ilyasekunov.posts.datasource.PostsDataSource
import ru.ilyasekunov.posts.datasource.PostsRemoteDataSource
import ru.ilyasekunov.posts.repository.PostsPagingRepository
import ru.ilyasekunov.posts.repository.PostsPagingRepositoryImpl
import ru.ilyasekunov.posts.repository.PostsRepository
import ru.ilyasekunov.posts.repository.PostsRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PostsDataModule {
    @Binds
    abstract fun bindPostsDataSource(
        postsRemoteDataSource: PostsRemoteDataSource
    ): PostsDataSource

    @Binds
    abstract fun bindPostsRepository(
        postsRepositoryImpl: PostsRepositoryImpl
    ): PostsRepository

    @Binds
    abstract fun bindPostsPagingRepository(
        postsPagingRepositoryImpl: PostsPagingRepositoryImpl
    ): PostsPagingRepository

    companion object {
        @Provides
        @Singleton
        fun providePostsApi(retrofit: Retrofit): PostsApi = retrofit.create()
    }
}