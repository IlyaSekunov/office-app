package ru.ilyasekunov.officeapp.data.datasource.local.mock

import ru.ilyasekunov.officeapp.data.datasource.UserDataSource
import ru.ilyasekunov.officeapp.data.dto.UserDto
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.User

class MockUserDataSource : UserDataSource {
    override suspend fun user(): Result<User?> = Result.success(User)

    override suspend fun saveChanges(userDto: UserDto): Result<Unit> {
        User = User?.copy(
            name = userDto.name,
            surname = userDto.surname,
            job = userDto.job,
            photo = userDto.photo ?: "",
            office = Offices.find { it.id == userDto.officeId }!!
        )
        return Result.success(Unit)
    }

    override suspend fun availableOffices(): Result<List<Office>> = Result.success(Offices)
}