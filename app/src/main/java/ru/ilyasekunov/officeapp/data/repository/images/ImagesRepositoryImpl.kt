package ru.ilyasekunov.officeapp.data.repository.images

import android.net.Uri
import ru.ilyasekunov.officeapp.data.ResponseResult
import ru.ilyasekunov.officeapp.data.datasource.ImagesUploaderDataSource
import ru.ilyasekunov.officeapp.data.datasource.ImageUrl

class ImagesRepositoryImpl(
    private val imageDataSource: ImagesUploaderDataSource
) : ImagesRepository {
    override suspend fun uploadImage(imageUri: Uri): ResponseResult<ImageUrl> =
        imageDataSource.uploadImage(imageUri)
}