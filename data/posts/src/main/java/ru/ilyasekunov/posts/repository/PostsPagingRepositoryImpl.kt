package ru.ilyasekunov.posts.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.ilyasekunov.dto.SearchPostsDto
import ru.ilyasekunov.model.IdeaPost
import ru.ilyasekunov.pager.PagingDataSource
import ru.ilyasekunov.posts.datasource.PostsDataSource
import javax.inject.Inject
import javax.inject.Singleton

private object PostsPagingDefaults {
    val PagingConfig = PagingConfig(
        pageSize = 20
    )
}

private object FavouritePostsPagingDefaults {
    val PagingConfig = PagingConfig(
        pageSize = 40
    )
}

@Singleton
internal class PostsPagingRepositoryImpl @Inject constructor(
    private val postsDataSource: PostsDataSource
) : PostsPagingRepository {
    override fun posts(searchPostsDto: SearchPostsDto): Flow<PagingData<IdeaPost>> {
        val pagingConfig = PostsPagingDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PagingDataSource(postsDataSource) { page ->
                posts(searchPostsDto, page, pageSize)
            }
        }.flow
    }

    override fun postsByAuthorId(authorId: Long): Flow<PagingData<IdeaPost>> {
        val pagingConfig = PostsPagingDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PagingDataSource(postsDataSource) { page ->
                postsByAuthorId(authorId, page, pageSize)
            }
        }.flow
    }

    override fun favouritePosts(searchPostsDto: SearchPostsDto): Flow<PagingData<IdeaPost>> {
        val pagingConfig = FavouritePostsPagingDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PagingDataSource(postsDataSource) { page ->
                favouritePosts(searchPostsDto, page, pageSize)
            }
        }.flow
    }

    override fun suggestedIdeas(): Flow<PagingData<IdeaPost>> {
        val pagingConfig = PostsPagingDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PagingDataSource(postsDataSource) { page ->
                suggestedIdeas(page, pageSize)
            }
        }.flow
    }

    override fun ideasInProgress(): Flow<PagingData<IdeaPost>> {
        val pagingConfig = PostsPagingDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PagingDataSource(postsDataSource) { page ->
                ideasInProgress(page, pageSize)
            }
        }.flow
    }

    override fun implementedIdeas(): Flow<PagingData<IdeaPost>> {
        val pagingConfig = PostsPagingDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PagingDataSource(postsDataSource) { page ->
                implementedIdeas(page, pageSize)
            }
        }.flow
    }

    override fun myIdeas(): Flow<PagingData<IdeaPost>> {
        val pagingConfig = FavouritePostsPagingDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PagingDataSource(postsDataSource) { page ->
                myIdeas(page, pageSize)
            }
        }.flow
    }
}