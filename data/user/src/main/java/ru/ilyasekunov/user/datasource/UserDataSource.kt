package ru.ilyasekunov.user.datasource

import ru.ilyasekunov.dto.UserDto

internal interface UserDataSource {
    suspend fun saveChanges(userDto: UserDto): Result<Unit>
}