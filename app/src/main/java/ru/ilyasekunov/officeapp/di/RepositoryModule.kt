package ru.ilyasekunov.officeapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ilyasekunov.officeapp.data.repository.user.UserRepository
import ru.ilyasekunov.officeapp.data.repository.user.UserRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun userRepository(): UserRepository = UserRepositoryImpl()
}