package ru.ilyasekunov.officeapp.data.datasource.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.ilyasekunov.officeapp.data.api.PostsApi
import ru.ilyasekunov.officeapp.data.datasource.PostsDataSource
import ru.ilyasekunov.officeapp.data.dto.EditPostDto
import ru.ilyasekunov.officeapp.data.dto.FiltersDto
import ru.ilyasekunov.officeapp.data.dto.PublishPostDto
import ru.ilyasekunov.officeapp.data.model.Filters
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.util.toResult

class PostsRemoteDataSource(
    private val postsApi: PostsApi,
    private val ioDispatcher: CoroutineDispatcher
) : PostsDataSource {
    override suspend fun publishPost(post: PublishPostDto): Result<Unit> =
        withContext(ioDispatcher) {
            postsApi.publishPost(post).toResult()
        }

    override suspend fun posts(
        filtersDto: FiltersDto,
        page: Int,
        pageSize: Int
    ): Result<List<IdeaPost>> =
        withContext(ioDispatcher) {
            postsApi.posts(filtersDto, page, pageSize).toResult()
        }

    override suspend fun editPostById(
        postId: Long, editedPost: EditPostDto
    ): Result<Unit> = withContext(ioDispatcher) {
        postsApi.editPostById(postId, editedPost).toResult()
    }

    override suspend fun deletePostById(postId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            postsApi.deletePostById(postId).toResult()
        }

    override suspend fun pressLike(postId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            postsApi.pressLike(postId).toResult()
        }

    override suspend fun removeLike(postId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            postsApi.removeLike(postId).toResult()
        }

    override suspend fun pressDislike(postId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            postsApi.pressDislike(postId).toResult()
        }

    override suspend fun removeDislike(postId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            postsApi.removeDislike(postId).toResult()
        }

    override suspend fun filters(): Result<Filters> =
        withContext(ioDispatcher) {
            postsApi.filters().toResult()
        }

    override suspend fun posts(): Result<List<IdeaPost>> =
        withContext(ioDispatcher) {
            postsApi.posts().toResult()
        }

    override suspend fun findPostById(postId: Long): Result<IdeaPost?> =
        withContext(ioDispatcher) {
            postsApi.findPostById(postId).toResult()
        }
}