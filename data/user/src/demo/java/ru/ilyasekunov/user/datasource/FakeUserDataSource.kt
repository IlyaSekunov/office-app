package ru.ilyasekunov.user.datasource

import kotlinx.coroutines.delay
import ru.ilyasekunov.dto.UserDto
import ru.ilyasekunov.test.offices
import ru.ilyasekunov.test.user
import javax.inject.Inject

internal class FakeUserDataSource @Inject constructor(): UserDataSource {
    override suspend fun saveChanges(userDto: UserDto): Result<Unit> {
        delay(1200L)

        user = user?.copy(
            name = userDto.name,
            surname = userDto.surname,
            job = userDto.job,
            photo = userDto.photo ?: "",
            office = offices.find { it.id == userDto.officeId }!!
        )

        return Result.success(Unit)
    }
}