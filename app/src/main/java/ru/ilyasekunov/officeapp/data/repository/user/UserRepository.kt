package ru.ilyasekunov.officeapp.data.repository.user

import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.User

interface UserRepository {
    suspend fun findUser(): User?
    suspend fun register(registrationForm: RegistrationForm)
}