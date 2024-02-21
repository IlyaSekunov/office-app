package ru.ilyasekunov.officeapp.data.repository.user

import ru.ilyasekunov.officeapp.data.dto.UserDto
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.User

interface UserRepository {
    suspend fun user(): User?
    suspend fun saveChanges(user: UserDto)
    suspend fun availableOffices(): List<Office>
}