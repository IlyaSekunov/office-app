package ru.ilyasekunov.posts.datasource

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.ilyasekunov.common.di.IoDispatcher
import ru.ilyasekunov.dto.EditPostDto
import ru.ilyasekunov.dto.PublishPostDto
import ru.ilyasekunov.dto.SearchPostsDto
import ru.ilyasekunov.model.Filters
import ru.ilyasekunov.model.IdeaPost
import ru.ilyasekunov.network.handleResponse
import ru.ilyasekunov.posts.api.PostsApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PostsRemoteDataSource @Inject constructor(
    private val postsApi: PostsApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PostsDataSource {
    override suspend fun publishPost(post: PublishPostDto): Result<Unit> =
        withContext(ioDispatcher) {
            handleResponse { postsApi.publishPost(post) }
        }

    override suspend fun posts(
        searchPostsDto: SearchPostsDto,
        page: Int,
        pageSize: Int
    ): Result<List<IdeaPost>> =
        withContext(ioDispatcher) {
            val officesId = searchPostsDto.filtersDto.offices
            val sortingFilterId = searchPostsDto.filtersDto.sortingFilter
            val search = searchPostsDto.searchDto.search
            handleResponse {
                postsApi.posts(
                    officesId = officesId,
                    sortingFilterId = sortingFilterId,
                    search = search,
                    page = page,
                    pageSize = pageSize
                )
            }
        }

    override suspend fun favouritePosts(
        searchPostsDto: SearchPostsDto,
        page: Int,
        pageSize: Int
    ): Result<List<IdeaPost>> = withContext(ioDispatcher) {
        val officesId = searchPostsDto.filtersDto.offices
        val sortingFilterId = searchPostsDto.filtersDto.sortingFilter
        val search = searchPostsDto.searchDto.search
        handleResponse {
            postsApi.favouritePosts(
                officesId = officesId,
                sortingFilterId = sortingFilterId,
                search = search,
                page = page,
                pageSize = pageSize
            )
        }
    }

    override suspend fun postsByAuthorId(authorId: Long, page: Int, pageSize: Int) =
        withContext(ioDispatcher) {
            handleResponse { postsApi.postsByAuthorId(authorId, page, pageSize) }
        }

    override suspend fun suggestedIdeas(page: Int, pageSize: Int): Result<List<IdeaPost>> =
        withContext(ioDispatcher) {
            handleResponse { postsApi.suggestedIdeas(page, pageSize) }
        }

    override suspend fun ideasInProgress(page: Int, pageSize: Int): Result<List<IdeaPost>> =
        withContext(ioDispatcher) {
            handleResponse { postsApi.ideasInProgress(page, pageSize) }
        }

    override suspend fun implementedIdeas(page: Int, pageSize: Int): Result<List<IdeaPost>> =
        withContext(ioDispatcher) {
            handleResponse { postsApi.implementedIdeas(page, pageSize) }
        }

    override suspend fun myIdeas(page: Int, pageSize: Int): Result<List<IdeaPost>> =
        withContext(ioDispatcher) {
            handleResponse { postsApi.myIdeas(page, pageSize) }
        }

    override suspend fun editPostById(
        postId: Long, editedPost: EditPostDto
    ): Result<Unit> = withContext(ioDispatcher) {
        handleResponse { postsApi.editPostById(postId, editedPost) }
    }

    override suspend fun deletePostById(postId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            handleResponse { postsApi.deletePostById(postId) }
        }

    override suspend fun pressLike(postId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            handleResponse { postsApi.pressLike(postId) }
        }

    override suspend fun removeLike(postId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            handleResponse { postsApi.removeLike(postId) }
        }

    override suspend fun pressDislike(postId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            handleResponse { postsApi.pressDislike(postId) }
        }

    override suspend fun removeDislike(postId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            handleResponse { postsApi.removeDislike(postId) }
        }

    override suspend fun filters(): Result<Filters> =
        withContext(ioDispatcher) {
            handleResponse { postsApi.filters() }
        }

    override suspend fun suggestIdeaToMyOffice(postId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            handleResponse { postsApi.suggestIdeaToMyOffice(postId) }
        }

    override suspend fun findPostById(postId: Long): Result<IdeaPost> =
        withContext(ioDispatcher) {
            handleResponse { postsApi.findPostById(postId) }
        }
}