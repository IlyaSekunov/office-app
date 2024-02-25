package ru.ilyasekunov.officeapp.data.api

import retrofit2.http.GET
import retrofit2.http.PATCH
import ru.ilyasekunov.officeapp.data.dto.UserDto
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.User

interface UserApi {
    @PATCH("users")
    suspend fun saveChanges(userDto: UserDto)
    @GET("users/available-offices")
    suspend fun availableOffices(): List<Office>
    suspend fun user(): User?
}