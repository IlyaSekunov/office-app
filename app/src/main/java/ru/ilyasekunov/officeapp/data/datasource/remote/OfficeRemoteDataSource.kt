package ru.ilyasekunov.officeapp.data.datasource.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.ilyasekunov.officeapp.data.ResponseResult
import ru.ilyasekunov.officeapp.data.api.OfficeApi
import ru.ilyasekunov.officeapp.data.datasource.OfficeDataSource
import ru.ilyasekunov.officeapp.data.model.Office

class OfficeRemoteDataSource(
    private val postsApi: OfficeApi,
    private val ioDispatcher: CoroutineDispatcher
) : OfficeDataSource {
    override suspend fun availableOffices(): ResponseResult<List<Office>> =
        withContext(ioDispatcher) {
            val response = postsApi.availableOffices()
            if (response.isSuccessful) {
                ResponseResult.success(response.body()!!)
            } else {
                ResponseResult.failure("Unknown network error")
            }
        }
}