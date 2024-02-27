package ru.ilyasekunov.officeapp.data.api

import retrofit2.Response
import retrofit2.http.GET
import ru.ilyasekunov.officeapp.data.model.Office

interface OfficeApi {
    @GET("offices")
    suspend fun availableOffices(): Response<List<Office>>
}