package ru.ilyasekunov.officeapp.data.repository.office

import ru.ilyasekunov.officeapp.data.ResponseResult
import ru.ilyasekunov.officeapp.data.datasource.OfficeDataSource
import ru.ilyasekunov.officeapp.data.model.Office

class OfficeRepositoryImpl(
    private val officeDataSource: OfficeDataSource
) : OfficeRepository {
    override suspend fun availableOffices(): ResponseResult<List<Office>> {
        return officeDataSource.availableOffices()
    }
}