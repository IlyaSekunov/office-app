package ru.ilyasekunov.officeapp.data.repository.posts

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.ilyasekunov.officeapp.data.datasource.PostsDataSource
import ru.ilyasekunov.officeapp.data.datasource.pager.PagingDataSource
import ru.ilyasekunov.officeapp.data.dto.SearchPostsDto
import ru.ilyasekunov.officeapp.data.model.IdeaPost

object PostsPagingDefaults {
    val PagingConfig = PagingConfig(
        pageSize = 20
    )
}

object FavouritePostsPagingDefaults {
    val PagingConfig = PagingConfig(
        pageSize = 40
    )
}

class PostsPagingRepository(private val postsDataSource: PostsDataSource) {
    fun posts(searchPostsDto: SearchPostsDto): Flow<PagingData<IdeaPost>> {
        val pagingConfig = PostsPagingDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PagingDataSource(postsDataSource) { page ->
                posts(searchPostsDto, page, pageSize)
            }
        }.flow
    }

    fun postsByAuthorId(authorId: Long): Flow<PagingData<IdeaPost>> {
        val pagingConfig = PostsPagingDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PagingDataSource(postsDataSource) { page ->
                postsByAuthorId(authorId, page, pageSize)
            }
        }.flow
    }

    fun favouritePosts(searchPostsDto: SearchPostsDto): Flow<PagingData<IdeaPost>> {
        val pagingConfig = FavouritePostsPagingDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PagingDataSource(postsDataSource) { page ->
                favouritePosts(searchPostsDto, page, pageSize)
            }
        }.flow
    }

    fun suggestedIdeas(): Flow<PagingData<IdeaPost>> {
        val pagingConfig = PostsPagingDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PagingDataSource(postsDataSource) { page ->
                suggestedIdeas(page, pageSize)
            }
        }.flow
    }

    fun ideasInProgress(): Flow<PagingData<IdeaPost>> {
        val pagingConfig = PostsPagingDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PagingDataSource(postsDataSource) { page ->
                ideasInProgress(page, pageSize)
            }
        }.flow
    }

    fun implementedIdeas(): Flow<PagingData<IdeaPost>> {
        val pagingConfig = PostsPagingDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PagingDataSource(postsDataSource) { page ->
                implementedIdeas(page, pageSize)
            }
        }.flow
    }

    fun myIdeas(): Flow<PagingData<IdeaPost>> {
        val pagingConfig = FavouritePostsPagingDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PagingDataSource(postsDataSource) { page ->
                myIdeas(page, pageSize)
            }
        }.flow
    }
}