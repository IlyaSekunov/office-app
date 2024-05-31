package ru.ilyasekunov.officeapp.data.datasource.local.mock

import kotlinx.coroutines.delay
import ru.ilyasekunov.officeapp.data.datasource.OfficeDataSource
import ru.ilyasekunov.officeapp.data.model.Office

class MockOfficeDataSource : OfficeDataSource {
    override suspend fun availableOffices(): Result<List<Office>> {
        delay(1200L)
        return Result.success(Offices)
    }
}