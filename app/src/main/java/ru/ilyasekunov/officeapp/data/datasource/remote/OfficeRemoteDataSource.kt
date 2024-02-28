package ru.ilyasekunov.officeapp.data.datasource.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.ilyasekunov.officeapp.data.api.OfficeApi
import ru.ilyasekunov.officeapp.data.datasource.OfficeDataSource
import ru.ilyasekunov.officeapp.data.model.Office

class OfficeRemoteDataSource(
    private val postsApi: OfficeApi,
    private val ioDispatcher: CoroutineDispatcher
) : OfficeDataSource {
    override suspend fun availableOffices(): Result<List<Office>> =
        withContext(ioDispatcher) {
            postsApi.availableOffices().run {
                if (isSuccessful) {
                    Result.success(body()!!)
                } else {
                    Result.failure(HttpException(this))
                }
            }
        }
}