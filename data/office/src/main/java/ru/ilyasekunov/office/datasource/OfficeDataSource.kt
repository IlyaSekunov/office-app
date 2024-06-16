package ru.ilyasekunov.office.datasource

import ru.ilyasekunov.model.Office

internal interface OfficeDataSource {
    suspend fun availableOffices(): Result<List<Office>>
}