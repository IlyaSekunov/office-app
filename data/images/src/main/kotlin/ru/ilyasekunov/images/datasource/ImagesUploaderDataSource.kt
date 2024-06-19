package ru.ilyasekunov.images.datasource

import android.net.Uri

typealias ImageUrl = String

interface ImagesUploaderDataSource {
    suspend fun uploadImage(imageUri: Uri): Result<ImageUrl>
}