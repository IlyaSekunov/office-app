package ru.ilyasekunov.officeapp.data.repository.user

import ru.ilyasekunov.officeapp.data.dto.UserDto
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.User

interface UserRepository {
    suspend fun user(): Result<User?>
    suspend fun saveChanges(user: UserDto): Result<Unit>
    suspend fun availableOffices(): Result<List<Office>>
}