package ru.ilyasekunov.posts.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.ilyasekunov.dto.SearchPostsDto
import ru.ilyasekunov.model.IdeaPost

interface PostsPagingRepository {
    fun posts(searchPostsDto: SearchPostsDto): Flow<PagingData<IdeaPost>>
    fun postsByAuthorId(authorId: Long): Flow<PagingData<IdeaPost>>
    fun favouritePosts(searchPostsDto: SearchPostsDto): Flow<PagingData<IdeaPost>>
    fun suggestedIdeas(): Flow<PagingData<IdeaPost>>
    fun ideasInProgress(): Flow<PagingData<IdeaPost>>
    fun implementedIdeas(): Flow<PagingData<IdeaPost>>
    fun myIdeas(): Flow<PagingData<IdeaPost>>
}