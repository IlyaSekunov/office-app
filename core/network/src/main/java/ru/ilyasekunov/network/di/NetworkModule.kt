package ru.ilyasekunov.network.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ilyasekunov.network.JsonLocalDateTimeDeserializer
import java.time.LocalDateTime
import javax.inject.Singleton

const val BASE_URl = "http://10.0.2.2:8189/api/"

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    @Singleton
    internal fun provideGson(): Gson =
        GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, JsonLocalDateTimeDeserializer())
            .create()
}