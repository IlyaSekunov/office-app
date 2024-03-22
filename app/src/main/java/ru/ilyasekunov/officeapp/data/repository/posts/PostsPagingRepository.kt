package ru.ilyasekunov.officeapp.data.repository.posts

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.ilyasekunov.officeapp.data.datasource.PostsDataSource
import ru.ilyasekunov.officeapp.data.datasource.remote.AuthorIdeasPagingDataSource
import ru.ilyasekunov.officeapp.data.datasource.remote.PostsPagingDataSource
import ru.ilyasekunov.officeapp.data.dto.SearchPostsDto
import ru.ilyasekunov.officeapp.data.model.IdeaPost

object PostsPagingDefaults {
    val PagingConfig = PagingConfig(
        pageSize = 10
    )
}

class PostsPagingRepository(private val postsDataSource: PostsDataSource) {
    fun posts(searchPostsDto: SearchPostsDto): Flow<PagingData<IdeaPost>> {
        return Pager(config = PostsPagingDefaults.PagingConfig) {
            PostsPagingDataSource(searchPostsDto, postsDataSource)
        }.flow
    }

    fun postsByAuthorId(authorId: Long): Flow<PagingData<IdeaPost>> {
        return Pager(config = PostsPagingDefaults.PagingConfig) {
            AuthorIdeasPagingDataSource(authorId, postsDataSource)
        }.flow
    }
}