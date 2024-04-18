package ru.ilyasekunov.officeapp.data.repository.comments

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.ilyasekunov.officeapp.data.datasource.CommentsDataSource
import ru.ilyasekunov.officeapp.data.datasource.remote.CommentsPagingDataSource
import ru.ilyasekunov.officeapp.data.model.Comment

object CommentsPagingDefaults {
    val PagingConfig = PagingConfig(
        pageSize = 20
    )
}

class CommentsPagingRepository(private val commentsDataSource: CommentsDataSource) {
    fun commentsByPostId(postId: Long, sortingFilterId: Int): Flow<PagingData<Comment>> {
        return Pager(config = CommentsPagingDefaults.PagingConfig) {
            CommentsPagingDataSource(postId, sortingFilterId, commentsDataSource)
        }.flow
    }
}