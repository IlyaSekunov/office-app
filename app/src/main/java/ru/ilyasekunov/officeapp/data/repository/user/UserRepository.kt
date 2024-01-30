package ru.ilyasekunov.officeapp.data.repository.user

import ru.ilyasekunov.officeapp.data.dto.RegistrationForm
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.User

interface UserRepository {
    fun findUser(): User?
    fun findOfficeList(): List<Office>
    fun register(registrationForm: RegistrationForm)
}