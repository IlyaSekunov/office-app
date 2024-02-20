package ru.ilyasekunov.officeapp.data.datasource

import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.User

interface UserDatasource {
    suspend fun user(): User?
    suspend fun register(registrationForm: RegistrationForm)
    suspend fun saveChanges(user: User)
}