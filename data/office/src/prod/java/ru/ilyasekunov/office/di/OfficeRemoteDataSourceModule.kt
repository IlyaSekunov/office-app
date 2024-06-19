package ru.ilyasekunov.office.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ilyasekunov.office.datasource.OfficeDataSource
import ru.ilyasekunov.office.datasource.OfficeRemoteDataSource

@Module
@InstallIn(SingletonComponent::class)
internal abstract class OfficeRemoteDataSourceModule {
    @Binds
    abstract fun bindOfficeDataSource(
        officeRemoteDataSource: OfficeRemoteDataSource
    ): OfficeDataSource
}