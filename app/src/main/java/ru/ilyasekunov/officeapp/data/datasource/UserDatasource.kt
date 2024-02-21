package ru.ilyasekunov.officeapp.data.datasource

import retrofit2.http.GET
import retrofit2.http.PATCH
import ru.ilyasekunov.officeapp.data.dto.UserDto
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.User

interface UserDatasource {
    suspend fun user(): User?
    @PATCH("users")
    suspend fun saveChanges(userDto: UserDto)
    @GET("users/available-offices")
    suspend fun availableOffices(): List<Office>
}