package ru.ilyasekunov.officeapp.data.datasource

import android.net.Uri
import ru.ilyasekunov.officeapp.data.ResponseResult

typealias ImageUrl = String

interface ImagesUploaderDataSource {
    suspend fun uploadImage(imageUri: Uri): ResponseResult<ImageUrl>
}