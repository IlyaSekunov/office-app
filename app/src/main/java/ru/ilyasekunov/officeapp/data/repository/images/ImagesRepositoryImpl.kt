package ru.ilyasekunov.officeapp.data.repository.images

import android.net.Uri
import ru.ilyasekunov.officeapp.data.datasource.ImageUrl
import ru.ilyasekunov.officeapp.data.datasource.ImagesUploaderDataSource

class ImagesRepositoryImpl(
    private val imageDataSource: ImagesUploaderDataSource
) : ImagesRepository {
    override suspend fun uploadImage(imageUri: Uri): Result<ImageUrl> =
        imageDataSource.uploadImage(imageUri)
}