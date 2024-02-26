package ru.ilyasekunov.officeapp.data.datasource.local.mock.user

import ru.ilyasekunov.officeapp.data.datasource.UserDatasource
import ru.ilyasekunov.officeapp.data.datasource.local.mock.Offices
import ru.ilyasekunov.officeapp.data.datasource.local.mock.User
import ru.ilyasekunov.officeapp.data.dto.UserDto

class MockUserDataSource : UserDatasource {
    override suspend fun user() = User

    override suspend fun saveChanges(userDto: UserDto) {
        User = User?.copy(
            name = userDto.name,
            surname = userDto.surname,
            job = userDto.job,
            photo = userDto.photo ?: "",
            office = Offices.find { it.id == userDto.officeId }!!
        )
    }

    override suspend fun availableOffices() = Offices
}