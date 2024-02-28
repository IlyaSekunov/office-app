package ru.ilyasekunov.officeapp.data.datasource

import android.net.Uri

typealias ImageUrl = String

interface ImagesUploaderDataSource {
    suspend fun uploadImage(imageUri: Uri): Result<ImageUrl>
}