package ru.ilyasekunov.office.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import ru.ilyasekunov.office.api.OfficeApi
import ru.ilyasekunov.office.datasource.OfficeDataSource
import ru.ilyasekunov.office.datasource.OfficeRemoteDataSource
import ru.ilyasekunov.office.repository.OfficeRepository
import ru.ilyasekunov.office.repository.OfficeRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class OfficeDataModule {
    @Binds
    abstract fun bindOfficeDataSource(
        officeRemoteDataSource: OfficeRemoteDataSource
    ): OfficeDataSource

    @Binds
    abstract fun bindOfficeRepository(
        officeRepositoryImpl: OfficeRepositoryImpl
    ): OfficeRepository

    companion object {
        @Provides
        @Singleton
        fun provideOfficeApi(retrofit: Retrofit): OfficeApi = retrofit.create()
    }
}