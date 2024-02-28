package ru.ilyasekunov.officeapp.data.repository.office

import ru.ilyasekunov.officeapp.data.datasource.OfficeDataSource
import ru.ilyasekunov.officeapp.data.model.Office

class OfficeRepositoryImpl(
    private val officeDataSource: OfficeDataSource
) : OfficeRepository {
    override suspend fun availableOffices(): Result<List<Office>> {
        return officeDataSource.availableOffices()
    }
}