package ru.ilyasekunov.user.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import ru.ilyasekunov.dto.UserDto

internal interface UserApi {
    @PATCH("users")
    suspend fun saveChanges(@Body userDto: UserDto): Response<Unit>
}