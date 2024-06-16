package ru.ilyasekunov.author.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.ilyasekunov.author.datasource.AuthorDataSource
import ru.ilyasekunov.model.IdeaAuthor
import ru.ilyasekunov.pager.PagingDataSource
import javax.inject.Inject
import javax.inject.Singleton

private object AuthorsPagingDefaults {
    val PagingConfig = PagingConfig(
        pageSize = 20
    )
}

@Singleton
internal class AuthorsPagingRepositoryImpl @Inject constructor(
    private val authorDataSource: AuthorDataSource
) : AuthorsPagingRepository {
    override fun officeEmployees(): Flow<PagingData<IdeaAuthor>> {
        val pagingConfig = AuthorsPagingDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PagingDataSource(authorDataSource) { page ->
                officeEmployees(page, pageSize)
            }
        }.flow
    }
}