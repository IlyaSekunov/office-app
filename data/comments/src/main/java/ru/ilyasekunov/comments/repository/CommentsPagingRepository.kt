package ru.ilyasekunov.comments.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.ilyasekunov.model.Comment

interface CommentsPagingRepository {
    fun commentsByPostId(postId: Long, sortingFilterId: Int): Flow<PagingData<Comment>>
}