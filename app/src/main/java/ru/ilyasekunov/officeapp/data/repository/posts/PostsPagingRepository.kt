package ru.ilyasekunov.officeapp.data.repository.posts

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.ilyasekunov.officeapp.data.datasource.PostsDataSource
import ru.ilyasekunov.officeapp.data.datasource.remote.PostsPagingDataSource
import ru.ilyasekunov.officeapp.data.dto.SearchPostsDto
import ru.ilyasekunov.officeapp.data.model.IdeaPost

object PostsPagingDefaults {
    val PagingConfig = PagingConfig(
        pageSize = 10
    )
}

object FavouritePostsPagingDefaults {
    val PagingConfig = PagingConfig(
        pageSize = 20
    )
}

class PostsPagingRepository(private val postsDataSource: PostsDataSource) {
    fun posts(searchPostsDto: SearchPostsDto): Flow<PagingData<IdeaPost>> {
        val pagingConfig = PostsPagingDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PostsPagingDataSource(postsDataSource) { page ->
                posts(searchPostsDto, page, pageSize)
            }
        }.flow
    }

    fun postsByAuthorId(authorId: Long): Flow<PagingData<IdeaPost>> {
        val pagingConfig = PostsPagingDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PostsPagingDataSource(postsDataSource) { page ->
                postsByAuthorId(authorId, page, pageSize)
            }
        }.flow
    }

    fun favouritePosts(searchPostsDto: SearchPostsDto): Flow<PagingData<IdeaPost>> {
        val pagingConfig = FavouritePostsPagingDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PostsPagingDataSource(postsDataSource) { page ->
                favouritePosts(searchPostsDto, page, pageSize)
            }
        }.flow
    }
}