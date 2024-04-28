package ru.ilyasekunov.officeapp.data.repository.comments

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.ilyasekunov.officeapp.data.datasource.CommentsDataSource
import ru.ilyasekunov.officeapp.data.datasource.pager.PagingDataSource
import ru.ilyasekunov.officeapp.data.model.Comment

object CommentsPagingDefaults {
    val PagingConfig = PagingConfig(
        pageSize = 20
    )
}

class CommentsPagingRepository(private val commentsDataSource: CommentsDataSource) {
    fun commentsByPostId(postId: Long, sortingFilterId: Int): Flow<PagingData<Comment>> {
        val pageConfig = CommentsPagingDefaults.PagingConfig
        return Pager(config = pageConfig) {
            val pageSize = pageConfig.pageSize
            PagingDataSource(commentsDataSource) { page ->
                commentsByPostId(
                    postId = postId,
                    sortingFilterId = sortingFilterId,
                    page = page,
                    pageSize = pageSize
                )
            }
        }.flow
    }
}