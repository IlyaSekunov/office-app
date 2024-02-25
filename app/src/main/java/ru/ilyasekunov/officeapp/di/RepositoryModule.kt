package ru.ilyasekunov.officeapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ilyasekunov.officeapp.data.datasource.AuthDataSource
import ru.ilyasekunov.officeapp.data.datasource.PostsDatasource
import ru.ilyasekunov.officeapp.data.datasource.TokenDataSource
import ru.ilyasekunov.officeapp.data.datasource.UserDatasource
import ru.ilyasekunov.officeapp.data.repository.auth.AuthRepository
import ru.ilyasekunov.officeapp.data.repository.auth.AuthRepositoryImpl
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
        @MockDataSource userDatasource: UserDatasource
    ): UserRepository = UserRepositoryImpl(userDatasource)

    @Provides
    @Singleton
    fun providePostsRepository(
        @MockDataSource postsDatasource: PostsDatasource
    ): PostsRepository = PostsRepositoryImpl(postsDatasource)

    @Provides
    @Singleton
    fun provideAuthRepository(
        @RemoteDataSource authDatasource: AuthDataSource,
        @LocalDataSource tokenDatasource: TokenDataSource
    ): AuthRepository = AuthRepositoryImpl(authDatasource, tokenDatasource)
}