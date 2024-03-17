package ru.ilyasekunov.officeapp.di

import android.content.ContentResolver
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import ru.ilyasekunov.officeapp.data.api.AuthApi
import ru.ilyasekunov.officeapp.data.api.AuthorApi
import ru.ilyasekunov.officeapp.data.api.ImgurApi
import ru.ilyasekunov.officeapp.data.api.OfficeApi
import ru.ilyasekunov.officeapp.data.api.PostsApi
import ru.ilyasekunov.officeapp.data.api.UserApi
import ru.ilyasekunov.officeapp.data.datasource.AuthDataSource
import ru.ilyasekunov.officeapp.data.datasource.AuthorDataSource
import ru.ilyasekunov.officeapp.data.datasource.ImagesUploaderDataSource
import ru.ilyasekunov.officeapp.data.datasource.OfficeDataSource
import ru.ilyasekunov.officeapp.data.datasource.PostsDataSource
import ru.ilyasekunov.officeapp.data.datasource.UserDataSource
import ru.ilyasekunov.officeapp.data.datasource.local.TokenLocalDataSource
import ru.ilyasekunov.officeapp.data.datasource.local.mock.MockAuthDataSource
import ru.ilyasekunov.officeapp.data.datasource.local.mock.MockAuthorDataSource
import ru.ilyasekunov.officeapp.data.datasource.local.mock.MockOfficeDataSource
import ru.ilyasekunov.officeapp.data.datasource.local.mock.MockPostsDataSource
import ru.ilyasekunov.officeapp.data.datasource.local.mock.MockUserDataSource
import ru.ilyasekunov.officeapp.data.datasource.remote.AuthRemoteDataSource
import ru.ilyasekunov.officeapp.data.datasource.remote.AuthorRemoteDataSource
import ru.ilyasekunov.officeapp.data.datasource.remote.ImgurRemoteDataSource
import ru.ilyasekunov.officeapp.data.datasource.remote.OfficeRemoteDataSource
import ru.ilyasekunov.officeapp.data.datasource.remote.PostsRemoteDataSource
import ru.ilyasekunov.officeapp.data.datasource.remote.UserRemoteDataSource
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {
    @Provides
    @Singleton
    @MockDataSource
    fun provideMockPostsDataSource(): PostsDataSource = MockPostsDataSource()

    @Provides
    @Singleton
    @RemoteDataSource
    fun providePostsRemoteDataSource(
        postsApi: PostsApi,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): PostsDataSource = PostsRemoteDataSource(postsApi, ioDispatcher)

    @Provides
    @Singleton
    @MockDataSource
    fun provideUserMockDataSource(): UserDataSource = MockUserDataSource()

    @Provides
    @Singleton
    @MockDataSource
    fun provideAuthMockDataSource(): AuthDataSource = MockAuthDataSource()

    @Provides
    @Singleton
    @RemoteDataSource
    fun provideUserRemoteDataSource(
        userApi: UserApi,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): UserDataSource = UserRemoteDataSource(userApi, ioDispatcher)

    @Provides
    @Singleton
    @LocalDataSource
    fun provideTokenLocalDataSource(
        dataStore: DataStore<Preferences>,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): TokenLocalDataSource = TokenLocalDataSource(dataStore, ioDispatcher)

    @Provides
    @RemoteDataSource
    fun provideAuthRemoteDataSource(
        authApi: AuthApi,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): AuthDataSource = AuthRemoteDataSource(authApi, ioDispatcher)

    @Provides
    @Singleton
    @RemoteDataSource
    fun provideImagesUploaderDataSource(
        imgurApi: ImgurApi,
        contentResolver: ContentResolver,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): ImagesUploaderDataSource = ImgurRemoteDataSource(imgurApi, contentResolver, ioDispatcher)

    @Provides
    @Singleton
    @MockDataSource
    fun provideMockOfficeDataSource(): OfficeDataSource = MockOfficeDataSource()

    @Provides
    @Singleton
    @RemoteDataSource
    fun provideOfficeRemoteDataSource(
        officeApi: OfficeApi,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): OfficeDataSource = OfficeRemoteDataSource(officeApi, ioDispatcher)

    @Provides
    @Singleton
    @MockDataSource
    fun provideMockAuthorDataSource(): AuthorDataSource = MockAuthorDataSource()

    @Provides
    @Singleton
    @RemoteDataSource
    fun provideAuthorRemoteDataSource(
        authorApi: AuthorApi,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): AuthorDataSource = AuthorRemoteDataSource(authorApi, ioDispatcher)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MockDataSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RemoteDataSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalDataSource