package ru.ilyasekunov.office.repository

import ru.ilyasekunov.model.Office
import ru.ilyasekunov.office.datasource.OfficeDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class OfficeRepositoryImpl @Inject constructor(
    private val officeDataSource: OfficeDataSource
) : OfficeRepository {
    override suspend fun availableOffices(): Result<List<Office>> {
        return officeDataSource.availableOffices()
    }
}