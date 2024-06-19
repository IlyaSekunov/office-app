package ru.ilyasekunov.office.api

import retrofit2.Response
import retrofit2.http.GET
import ru.ilyasekunov.model.Office

internal interface OfficeApi {
    @GET("users/offices")
    suspend fun availableOffices(): Response<List<Office>>
}