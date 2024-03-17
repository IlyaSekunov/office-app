package ru.ilyasekunov.officeapp.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import ru.ilyasekunov.officeapp.data.dto.UserDto

interface UserApi {
    @PATCH("users")
    suspend fun saveChanges(@Body userDto: UserDto): Response<Unit>
}