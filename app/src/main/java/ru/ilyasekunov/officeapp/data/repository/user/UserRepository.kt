package ru.ilyasekunov.officeapp.data.repository.user

import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.User

interface UserRepository {
    suspend fun user(): User?
    suspend fun register(registrationForm: RegistrationForm)
    suspend fun saveChanges(user: User)
}