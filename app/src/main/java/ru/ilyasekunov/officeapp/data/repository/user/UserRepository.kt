package ru.ilyasekunov.officeapp.data.repository.user

import ru.ilyasekunov.officeapp.data.dto.UserDto

interface UserRepository {
    suspend fun saveChanges(user: UserDto): Result<Unit>
}