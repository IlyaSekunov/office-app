package ru.ilyasekunov.officeapp.data.repository.author

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.ilyasekunov.officeapp.data.datasource.AuthorDataSource
import ru.ilyasekunov.officeapp.data.datasource.pager.PagingDataSource
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor

object AuthorsPagingDefaults {
    val PagingConfig = PagingConfig(
        pageSize = 20
    )
}

class AuthorsPagingRepository(private val authorDataSource: AuthorDataSource) {
    fun officeEmployees(): Flow<PagingData<IdeaAuthor>> {
        val pagingConfig = AuthorsPagingDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PagingDataSource(authorDataSource) { page ->
                officeEmployees(page, pageSize)
            }
        }.flow
    }
}