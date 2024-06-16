package ru.ilyasekunov.images.repository

import android.net.Uri
import ru.ilyasekunov.images.datasource.ImageUrl

interface ImagesRepository {
    suspend fun uploadImage(imageUri: Uri): Result<ImageUrl>
}