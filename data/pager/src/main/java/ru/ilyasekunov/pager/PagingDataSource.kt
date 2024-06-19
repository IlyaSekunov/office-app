package ru.ilyasekunov.pager

import androidx.paging.PagingSource
import androidx.paging.PagingState

class PagingDataSource<DataSource, Model : Any>(
    private val dataSource: DataSource,
    private val request: suspend DataSource.(page: Int) -> Result<List<Model>>
) : PagingSource<Int, Model>() {
    override fun getRefreshKey(state: PagingState<Int, Model>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Model> {
        val page = params.key ?: 1
        val requestResult = dataSource.request(page)
        if (requestResult.isSuccess) {
            val result = requestResult.getOrThrow()
            return LoadResult.Page(
                data = result,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (result.isEmpty()) null else page + 1
            )
        }
        return LoadResult.Error(requestResult.exceptionOrNull()!!)
    }
}