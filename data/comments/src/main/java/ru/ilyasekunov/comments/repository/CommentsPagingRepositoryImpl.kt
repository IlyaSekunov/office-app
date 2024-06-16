package ru.ilyasekunov.comments.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.ilyasekunov.comments.datasource.CommentsDataSource
import ru.ilyasekunov.model.Comment
import ru.ilyasekunov.pager.PagingDataSource
import javax.inject.Inject
import javax.inject.Singleton

private object CommentsPagingDefaults {
    val PagingConfig = PagingConfig(
        pageSize = 40
    )
}

@Singleton
internal class CommentsPagingRepositoryImpl @Inject constructor(
    private val commentsDataSource: CommentsDataSource
) : CommentsPagingRepository {
    override fun commentsByPostId(postId: Long, sortingFilterId: Int): Flow<PagingData<Comment>> {
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