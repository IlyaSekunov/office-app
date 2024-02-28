package ru.ilyasekunov.officeapp.data.datasource

import ru.ilyasekunov.officeapp.data.model.Office

interface OfficeDataSource {
    suspend fun availableOffices(): Result<List<Office>>
}