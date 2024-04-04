package ru.ilyasekunov.officeapp.data.datasource.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.ilyasekunov.officeapp.data.datasource.CommentsDataSource
import ru.ilyasekunov.officeapp.data.model.Comment
import ru.ilyasekunov.officeapp.data.repository.comments.CommentsPagingDefaults

class CommentsPagingDataSource(
    private val postId: Long,
    private val commentsDataSource: CommentsDataSource
) : PagingSource<Int, Comment>() {
    override fun getRefreshKey(state: PagingState<Int, Comment>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> {
        val page = params.key ?: 1
        val pageSize = CommentsPagingDefaults.PagingConfig.pageSize
        val commentsResult = commentsDataSource.commentsByPostId(postId, page, pageSize)
        if (commentsResult.isSuccess) {
            val comments = commentsResult.getOrThrow()
            return LoadResult.Page(
                data = comments,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (comments.isEmpty()) null else page + 1
            )
        }
        return LoadResult.Error(commentsResult.exceptionOrNull()!!)
    }
}