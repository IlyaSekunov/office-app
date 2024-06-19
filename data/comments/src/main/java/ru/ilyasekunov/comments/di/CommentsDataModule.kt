package ru.ilyasekunov.comments.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import ru.ilyasekunov.comments.api.CommentsApi
import ru.ilyasekunov.comments.repository.CommentsPagingRepository
import ru.ilyasekunov.comments.repository.CommentsPagingRepositoryImpl
import ru.ilyasekunov.comments.repository.CommentsRepository
import ru.ilyasekunov.comments.repository.CommentsRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CommentsDataModule {
    @Binds
    abstract fun bindCommentsRepository(
        commentsRepositoryImpl: CommentsRepositoryImpl
    ): CommentsRepository

    @Binds
    abstract fun bindCommentsPagingRepository(
        commentsPagingRepositoryImpl: CommentsPagingRepositoryImpl
    ): CommentsPagingRepository

    companion object {
        @Provides
        @Singleton
        fun provideCommentsApi(retrofit: Retrofit): CommentsApi = retrofit.create()
    }
}