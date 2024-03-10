package ru.ilyasekunov.officeapp.data.repository.user

import ru.ilyasekunov.officeapp.data.dto.UserDto
import ru.ilyasekunov.officeapp.data.model.Office

interface UserRepository {
    suspend fun saveChanges(user: UserDto): Result<Unit>
    suspend fun availableOffices(): Result<List<Office>>
}