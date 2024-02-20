package ru.ilyasekunov.officeapp.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import ru.ilyasekunov.officeapp.data.datasource.TokenDatasource
import ru.ilyasekunov.officeapp.data.datasource.local.TokenLocalDatasourceImpl
import ru.ilyasekunov.officeapp.data.datasource.local.mock.posts.MockPostsDatasource
import ru.ilyasekunov.officeapp.data.datasource.local.mock.user.MockUserDatasource
import ru.ilyasekunov.officeapp.data.datasource.AuthDatasource
import ru.ilyasekunov.officeapp.data.datasource.PostsDatasource
import ru.ilyasekunov.officeapp.data.datasource.UserDatasource
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
    fun provideUserLocalDatasource(dataStore: DataStore<Preferences>): TokenDatasource =
        TokenLocalDatasourceImpl(dataStore)

    @Provides
    @Singleton
    fun provideAuthDatasource(retrofit: Retrofit): AuthDatasource =
        retrofit.create(AuthDatasource::class.java)
}