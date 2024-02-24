package ru.ilyasekunov.officeapp.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import ru.ilyasekunov.officeapp.data.api.AuthApi
import ru.ilyasekunov.officeapp.data.api.PostsApi
import ru.ilyasekunov.officeapp.data.datasource.PostsDatasource
import ru.ilyasekunov.officeapp.data.datasource.UserDatasource
import ru.ilyasekunov.officeapp.data.datasource.local.TokenLocalDataSource
import ru.ilyasekunov.officeapp.data.datasource.local.mock.posts.MockPostsDataSource
import ru.ilyasekunov.officeapp.data.datasource.local.mock.user.MockUserDataSource
import ru.ilyasekunov.officeapp.data.datasource.remote.AuthRemoteDataSource
import ru.ilyasekunov.officeapp.data.datasource.remote.PostsRemoteDataSource
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {
    @Provides
    @Singleton
    @MockDataSource
    fun provideMockPostsDataSource(): PostsDatasource = MockPostsDataSource()

    @Provides
    @Singleton
    @RemoteDataSource
    fun providePostsRemoteDataSource(
        postsApi: PostsApi,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): PostsDatasource = PostsRemoteDataSource(postsApi, ioDispatcher)

    @Provides
    @Singleton
    @MockDataSource
    fun provideUserMockDataSource(): UserDatasource = MockUserDataSource()

    @Provides
    @Singleton
    fun provideTokenLocalDataSource(
        dataStore: DataStore<Preferences>,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): TokenLocalDataSource = TokenLocalDataSource(dataStore, ioDispatcher)

    @Provides
    @Singleton
    fun provideAuthRemoteDataSource(
        authApi: AuthApi,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): AuthRemoteDataSource = AuthRemoteDataSource(authApi, ioDispatcher)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MockDataSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RemoteDataSource