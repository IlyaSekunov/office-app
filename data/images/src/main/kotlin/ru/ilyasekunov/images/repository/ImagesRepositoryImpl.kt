package ru.ilyasekunov.images.repository

import android.net.Uri
import ru.ilyasekunov.images.datasource.ImageUrl
import ru.ilyasekunov.images.datasource.ImagesUploaderDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ImagesRepositoryImpl @Inject constructor(
    private val imageDataSource: ImagesUploaderDataSource
) : ImagesRepository {
    override suspend fun uploadImage(imageUri: Uri): Result<ImageUrl> =
        imageDataSource.uploadImage(imageUri)
}