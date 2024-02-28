package ru.ilyasekunov.officeapp.data.repository.images

import android.net.Uri
import ru.ilyasekunov.officeapp.data.datasource.ImageUrl

interface ImagesRepository {
    suspend fun uploadImage(imageUri: Uri): Result<ImageUrl>
}