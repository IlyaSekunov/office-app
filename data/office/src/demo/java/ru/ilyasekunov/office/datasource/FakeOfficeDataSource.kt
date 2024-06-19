package ru.ilyasekunov.office.datasource

import kotlinx.coroutines.delay
import ru.ilyasekunov.model.Office
import ru.ilyasekunov.test.offices
import javax.inject.Inject

internal class FakeOfficeDataSource @Inject constructor(): OfficeDataSource {
    override suspend fun availableOffices(): Result<List<Office>> {
        delay(1200L)
        return Result.success(offices)
    }
}