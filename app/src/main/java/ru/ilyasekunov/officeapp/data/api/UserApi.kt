package ru.ilyasekunov.officeapp.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import ru.ilyasekunov.officeapp.data.dto.UserDto
import ru.ilyasekunov.officeapp.data.model.Office

interface UserApi {
    @PATCH("users")
    suspend fun saveChanges(userDto: UserDto): Response<Unit>
    @GET("users/available-offices")
    suspend fun availableOffices(): Response<List<Office>>
}