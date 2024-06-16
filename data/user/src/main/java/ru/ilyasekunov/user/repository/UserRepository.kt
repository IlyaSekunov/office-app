package ru.ilyasekunov.user.repository

import ru.ilyasekunov.dto.UserDto

interface UserRepository {
    suspend fun saveChanges(user: UserDto): Result<Unit>
}