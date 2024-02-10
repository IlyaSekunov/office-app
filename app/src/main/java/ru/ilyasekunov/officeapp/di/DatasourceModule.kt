package ru.ilyasekunov.officeapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ilyasekunov.officeapp.data.datasource.local.mock.posts.MockPostsDatasource
import ru.ilyasekunov.officeapp.data.datasource.PostsDatasource
import ru.ilyasekunov.officeapp.data.datasource.UserDatasource
import ru.ilyasekunov.officeapp.data.datasource.local.mock.user.MockUserDatasource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {
    @Provides
    @Singleton
    fun postsDatasource(): PostsDatasource = MockPostsDatasource()

    @Provides
    @Singleton
    fun userDatasource(): UserDatasource = MockUserDatasource()
}