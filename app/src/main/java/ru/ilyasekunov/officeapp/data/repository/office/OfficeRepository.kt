package ru.ilyasekunov.officeapp.data.repository.office

import ru.ilyasekunov.officeapp.data.ResponseResult
import ru.ilyasekunov.officeapp.data.model.Office

interface OfficeRepository {
    suspend fun availableOffices(): ResponseResult<List<Office>>
}