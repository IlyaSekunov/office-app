package ru.ilyasekunov.officeapp.data.repository.author

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.ilyasekunov.officeapp.data.datasource.AuthorDataSource
import ru.ilyasekunov.officeapp.data.datasource.remote.AuthorsPagingDataSource
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor

object AuthorsPagingDefaults {
    val PagingConfig = PagingConfig(
        pageSize = 10
    )
}

class AuthorsPagingRepository(private val authorDataSource: AuthorDataSource) {
    fun officeWorkers(): Flow<PagingData<IdeaAuthor>> {
        val pagingConfig = AuthorsPagingDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            AuthorsPagingDataSource(authorDataSource) { page ->
                officeEmployees(page, pageSize)
            }
        }.flow
    }
}