package ru.ilyasekunov.officeapp.data.datasource.local.mock

import ru.ilyasekunov.officeapp.data.ResponseResult
import ru.ilyasekunov.officeapp.data.datasource.OfficeDataSource
import ru.ilyasekunov.officeapp.data.model.Office

class MockOfficeDataSource : OfficeDataSource {
    override suspend fun availableOffices(): ResponseResult<List<Office>> = ResponseResult.success(Offices)
}