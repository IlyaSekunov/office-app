package ru.ilyasekunov.office.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ilyasekunov.office.datasource.FakeOfficeDataSource
import ru.ilyasekunov.office.datasource.OfficeDataSource

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FakeOfficeDataSourceModule {
    @Binds
    abstract fun bindOfficeDataSource(
        fakeOfficeDataSource: FakeOfficeDataSource
    ): OfficeDataSource
}