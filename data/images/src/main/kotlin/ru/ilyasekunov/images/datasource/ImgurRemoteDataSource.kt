package ru.ilyasekunov.images.datasource

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import ru.ilyasekunov.common.di.IoDispatcher
import ru.ilyasekunov.images.api.ImgurApi
import ru.ilyasekunov.network.handleResult
import ru.ilyasekunov.util.copyUriContentToFile
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ImgurRemoteDataSource @Inject constructor(
    private val imgurApi: ImgurApi,
    private val contentResolver: ContentResolver,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
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
                "image",
                imageFile.name,
                RequestBody.create(null, imageFile)
            )

            handleResult {
                imgurApi.uploadFile(
                    image = imagePart,
                    name = RequestBody.create(null, imageFile.name)
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