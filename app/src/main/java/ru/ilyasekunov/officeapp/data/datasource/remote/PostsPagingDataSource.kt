package ru.ilyasekunov.officeapp.data.datasource.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.ilyasekunov.officeapp.data.datasource.PostsDataSource
import ru.ilyasekunov.officeapp.data.dto.SearchPostsDto
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.repository.posts.PostsPagingDefaults

class PostsPagingDataSource(
    private val searchPostsDto: SearchPostsDto,
    private val postsDataSource: PostsDataSource
) : PagingSource<Int, IdeaPost>() {
    override fun getRefreshKey(state: PagingState<Int, IdeaPost>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, IdeaPost> {
        val page = params.key ?: 1
        val pageSize = PostsPagingDefaults.PagingConfig.pageSize
        val postsResult = postsDataSource.posts(searchPostsDto, page, pageSize)
        if (postsResult.isSuccess) {
            val posts = postsResult.getOrThrow()
            return LoadResult.Page(
                data = posts,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (posts.isEmpty()) null else page + 1
            )
        }
        return LoadResult.Error(Exception())
    }
}