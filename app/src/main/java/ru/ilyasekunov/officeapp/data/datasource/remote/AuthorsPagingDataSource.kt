package ru.ilyasekunov.officeapp.data.datasource.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.ilyasekunov.officeapp.data.datasource.AuthorDataSource
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor

class AuthorsPagingDataSource(
    private val authorDataSource: AuthorDataSource,
    private val request: suspend AuthorDataSource.(page: Int) -> Result<List<IdeaAuthor>>
) : PagingSource<Int, IdeaAuthor>() {
    override fun getRefreshKey(state: PagingState<Int, IdeaAuthor>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, IdeaAuthor> {
        val page = params.key ?: 1
        val authorsResult = authorDataSource.request(page)
        if (authorsResult.isSuccess) {
            val authors = authorsResult.getOrThrow()
            return LoadResult.Page(
                data = authors,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (authors.isEmpty()) null else page + 1
            )
        }
        return LoadResult.Error(authorsResult.exceptionOrNull()!!)
    }
}