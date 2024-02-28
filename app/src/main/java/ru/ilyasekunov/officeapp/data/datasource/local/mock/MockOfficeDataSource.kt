package ru.ilyasekunov.officeapp.data.datasource.local.mock

import ru.ilyasekunov.officeapp.data.datasource.OfficeDataSource
import ru.ilyasekunov.officeapp.data.model.Office

class MockOfficeDataSource : OfficeDataSource {
    override suspend fun availableOffices(): Result<List<Office>> = Result.success(Offices)
}