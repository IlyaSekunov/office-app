package ru.ilyasekunov.office.datasource

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.ilyasekunov.common.di.IoDispatcher
import ru.ilyasekunov.model.Office
import ru.ilyasekunov.network.handleResponse
import ru.ilyasekunov.office.api.OfficeApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class OfficeRemoteDataSource @Inject constructor(
    private val postsApi: OfficeApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : OfficeDataSource {
    override suspend fun availableOffices(): Result<List<Office>> =
        withContext(ioDispatcher) {
            handleResponse { postsApi.availableOffices() }
        }
}