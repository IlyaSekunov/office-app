package ru.ilyasekunov.officeapp.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import ru.ilyasekunov.officeapp.data.api.AuthApi
import ru.ilyasekunov.officeapp.data.datasource.PostsDatasource
import ru.ilyasekunov.officeapp.data.datasource.UserDatasource
import ru.ilyasekunov.officeapp.data.datasource.local.TokenLocalDatasource
import ru.ilyasekunov.officeapp.data.datasource.local.mock.posts.MockPostsDatasource
import ru.ilyasekunov.officeapp.data.datasource.local.mock.user.MockUserDatasource
import ru.ilyasekunov.officeapp.data.datasource.remote.AuthRemoteDatasource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {
    @Provides
    @Singleton
    fun providePostsDatasource(): PostsDatasource = MockPostsDatasource()

    @Provides
    @Singleton
    fun provideUserRemoteDatasource(): UserDatasource = MockUserDatasource()

    @Provides
    @Singleton
    fun provideTokenLocalDatasource(
        dataStore: DataStore<Preferences>,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): TokenLocalDatasource = TokenLocalDatasource(dataStore, ioDispatcher)

    @Provides
    @Singleton
    fun provideAuthRemoteDatasource(
        authApi: AuthApi,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): AuthRemoteDatasource = AuthRemoteDatasource(authApi, ioDispatcher)
}