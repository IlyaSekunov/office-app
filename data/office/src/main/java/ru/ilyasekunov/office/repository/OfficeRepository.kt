package ru.ilyasekunov.office.repository

import ru.ilyasekunov.model.Office

interface OfficeRepository {
    suspend fun availableOffices(): Result<List<Office>>
}