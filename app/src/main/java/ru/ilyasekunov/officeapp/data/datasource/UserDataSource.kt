package ru.ilyasekunov.officeapp.data.datasource

import ru.ilyasekunov.officeapp.data.dto.UserDto

interface UserDataSource {
    suspend fun saveChanges(userDto: UserDto): Result<Unit>
}