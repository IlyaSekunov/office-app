package ru.ilyasekunov.officeapp.data.datasource.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.ilyasekunov.officeapp.data.api.PostsApi
import ru.ilyasekunov.officeapp.data.datasource.PostsDataSource
import ru.ilyasekunov.officeapp.data.dto.EditPostDto
import ru.ilyasekunov.officeapp.data.dto.FiltersDto
import ru.ilyasekunov.officeapp.data.dto.PublishPostDto
import ru.ilyasekunov.officeapp.data.dto.SearchDto
import ru.ilyasekunov.officeapp.data.model.Filters
import ru.ilyasekunov.officeapp.data.model.IdeaPost

class PostsRemoteDataSource(
    private val postsApi: PostsApi,
    private val ioDispatcher: CoroutineDispatcher
) : PostsDataSource {
    override suspend fun publishPost(post: PublishPostDto): Result<Unit> =
        withContext(ioDispatcher) {
            handleResponse { postsApi.publishPost(post) }
        }

    override suspend fun posts(
        filtersDto: FiltersDto,
        searchDto: SearchDto,
        page: Int,
        pageSize: Int
    ): Result<List<IdeaPost>> =
        withContext(ioDispatcher) {
            handleResponse { postsApi.posts(filtersDto, searchDto, page, pageSize) }
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

    override suspend fun posts(): Result<List<IdeaPost>> =
        withContext(ioDispatcher) {
            handleResponse { postsApi.posts() }
        }

    override suspend fun findPostById(postId: Long): Result<IdeaPost?> =
        withContext(ioDispatcher) {
            handleResponse { postsApi.findPostById(postId) }
        }
}