package ru.ilyasekunov.officeapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ilyasekunov.officeapp.data.datasource.AuthDataSource
import ru.ilyasekunov.officeapp.data.datasource.AuthorDataSource
import ru.ilyasekunov.officeapp.data.datasource.CommentsDataSource
import ru.ilyasekunov.officeapp.data.datasource.ImagesUploaderDataSource
import ru.ilyasekunov.officeapp.data.datasource.OfficeDataSource
import ru.ilyasekunov.officeapp.data.datasource.PostsDataSource
import ru.ilyasekunov.officeapp.data.datasource.UserDataSource
import ru.ilyasekunov.officeapp.data.datasource.local.TokenLocalDataSource
import ru.ilyasekunov.officeapp.data.repository.auth.AuthRepository
import ru.ilyasekunov.officeapp.data.repository.auth.AuthRepositoryImpl
import ru.ilyasekunov.officeapp.data.repository.author.AuthorRepository
import ru.ilyasekunov.officeapp.data.repository.author.AuthorRepositoryImpl
import ru.ilyasekunov.officeapp.data.repository.comments.CommentsPagingRepository
import ru.ilyasekunov.officeapp.data.repository.comments.CommentsRepository
import ru.ilyasekunov.officeapp.data.repository.comments.CommentsRepositoryImpl
import ru.ilyasekunov.officeapp.data.repository.images.ImagesRepository
import ru.ilyasekunov.officeapp.data.repository.images.ImagesRepositoryImpl
import ru.ilyasekunov.officeapp.data.repository.office.OfficeRepository
import ru.ilyasekunov.officeapp.data.repository.office.OfficeRepositoryImpl
import ru.ilyasekunov.officeapp.data.repository.posts.PostsPagingRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepository
import ru.ilyasekunov.officeapp.data.repository.posts.PostsRepositoryImpl
import ru.ilyasekunov.officeapp.data.repository.user.UserRepository
import ru.ilyasekunov.officeapp.data.repository.user.UserRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideUserRepository(
        @MockDataSource userDatasource: UserDataSource
        //@RemoteDataSource userDatasource: UserDataSource
    ): UserRepository = UserRepositoryImpl(userDatasource)

    @Provides
    @Singleton
    fun providePostsRepository(
        @MockDataSource postsDatasource: PostsDataSource
        //@RemoteDataSource postsDatasource: PostsDataSource
    ): PostsRepository = PostsRepositoryImpl(postsDatasource)

    @Provides
    @Singleton
    fun provideAuthRepository(
        @MockDataSource authDatasource: AuthDataSource,
        //@RemoteDataSource authDatasource: AuthDataSource,
        @LocalDataSource tokenDatasource: TokenLocalDataSource
    ): AuthRepository = AuthRepositoryImpl(authDatasource, tokenDatasource)

    @Provides
    @Singleton
    fun provideImagesRepository(
        @RemoteDataSource imagesUploaderDataSource: ImagesUploaderDataSource
    ): ImagesRepository = ImagesRepositoryImpl(imagesUploaderDataSource)

    @Provides
    @Singleton
    fun provideOfficeRepository(
        @MockDataSource officeDataSource: OfficeDataSource
        //@RemoteDataSource officeDataSource: OfficeDataSource
    ): OfficeRepository = OfficeRepositoryImpl(officeDataSource)

    @Provides
    @Singleton
    fun providePostsPagingRepository(
        @MockDataSource postsDatasource: PostsDataSource
        //@RemoteDataSource postsDatasource: PostsDataSource
    ): PostsPagingRepository = PostsPagingRepository(postsDatasource)

    @Provides
    @Singleton
    fun provideAuthorRepository(
        @RemoteDataSource authorDataSource: AuthorDataSource
    ): AuthorRepository = AuthorRepositoryImpl(authorDataSource)

    @Provides
    @Singleton
    fun provideCommentsRepository(
        //@RemoteDataSource commentsDataSource: CommentsDataSource
        @MockDataSource commentsDataSource: CommentsDataSource
    ): CommentsRepository = CommentsRepositoryImpl(commentsDataSource)

    @Provides
    @Singleton
    fun provideCommentsPagingDataSource(
        //@RemoteDataSource commentsDataSource: CommentsDataSource
        @MockDataSource commentsDataSource: CommentsDataSource
    ): CommentsPagingRepository = CommentsPagingRepository(commentsDataSource)
}