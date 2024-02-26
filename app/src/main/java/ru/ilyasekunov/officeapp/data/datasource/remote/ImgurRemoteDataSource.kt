package ru.ilyasekunov.officeapp.data.datasource.remote

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.ilyasekunov.officeapp.data.ResponseResult
import ru.ilyasekunov.officeapp.data.api.ImgurApi
import ru.ilyasekunov.officeapp.data.datasource.ImagesUploaderDataSource
import ru.ilyasekunov.officeapp.data.datasource.ImageUrl
import ru.ilyasekunov.officeapp.util.copyStreamToFile

class ImgurRemoteDataSource(
    private val imgurApi: ImgurApi,
    private val contentResolver: ContentResolver,
    private val ioDispatcher: CoroutineDispatcher
) : ImagesUploaderDataSource {
    override suspend fun uploadImage(imageUri: Uri): ResponseResult<ImageUrl> =
        withContext(ioDispatcher) {
            val imageFile = try {
                imageUri.copyStreamToFile(contentResolver)
            } catch (e: Exception) {
                return@withContext ResponseResult.failure("Error while converting uri to file")
            }

            val imagePart = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                imageFile.asRequestBody()
            )

            val response = imgurApi.uploadFile(
                image = imagePart,
                name = imageFile.name.toRequestBody()
            )

            if (response.isSuccessful) {
                ResponseResult.success(response.body()!!.upload.link)
            } else {
                ResponseResult.failure("Unknown network error")
            }
        }
}