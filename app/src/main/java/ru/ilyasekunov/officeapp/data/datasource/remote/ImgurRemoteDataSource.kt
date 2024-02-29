package ru.ilyasekunov.officeapp.data.datasource.remote

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import ru.ilyasekunov.officeapp.data.api.ImgurApi
import ru.ilyasekunov.officeapp.data.datasource.ImageUrl
import ru.ilyasekunov.officeapp.data.datasource.ImagesUploaderDataSource
import ru.ilyasekunov.officeapp.util.copyUriContentToFile
import java.io.File

class ImgurRemoteDataSource(
    private val imgurApi: ImgurApi,
    private val contentResolver: ContentResolver,
    private val ioDispatcher: CoroutineDispatcher
) : ImagesUploaderDataSource {
    override suspend fun uploadImage(imageUri: Uri): Result<ImageUrl> =
        withContext(ioDispatcher) {
            val imageFile = File.createTempFile("temp", null)
            copyUriContentToFile(
                source = imageUri,
                destination = imageFile,
                contentResolver = contentResolver
            )

            val imagePart = MultipartBody.Part.createFormData(
                name = "image",
                filename = imageFile.name,
                body = imageFile.asRequestBody()
            )

            handleResult {
                imgurApi.uploadFile(
                    image = imagePart,
                    name = imageFile.name.toRequestBody()
                ).run {
                    if (isSuccessful) {
                        Result.success(body()!!.upload.link)
                    } else {
                        Result.failure(HttpException(this))
                    }
                }
            }
        }
}