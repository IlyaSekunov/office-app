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

class PostsRemoteDataSource(
    private val postsApi: PostsApi,
    private val ioDispatcher: CoroutineDispatcher
) : PostsDataSource {
    override suspend fun publishPost(post: PublishPostDto) =
        withContext(ioDispatcher) {
            postsApi.publishPost(post)
        }

    override suspend fun posts(
        filtersDto: FiltersDto,
        page: Int,
        pageSize: Int
    ): List<IdeaPost> = withContext(ioDispatcher) {
        postsApi.posts(filtersDto, page, pageSize)
    }

    override suspend fun editPostById(
        postId: Long, editedPost: EditPostDto
    ) = withContext(ioDispatcher) {
        postsApi.editPostById(postId, editedPost)
    }

    override suspend fun deletePostById(postId: Long) =
        withContext(ioDispatcher) {
            postsApi.deletePostById(postId)
        }

    override suspend fun pressLike(postId: Long, userId: Long) =
        withContext(ioDispatcher) {
            postsApi.pressLike(postId, userId)
        }

    override suspend fun removeLike(postId: Long, userId: Long) =
        withContext(ioDispatcher) {
            postsApi.removeLike(postId, userId)
        }

    override suspend fun pressDislike(postId: Long, userId: Long) =
        withContext(ioDispatcher) {
            postsApi.pressDislike(postId, userId)
        }

    override suspend fun removeDislike(postId: Long, userId: Long) =
        withContext(ioDispatcher) {
            postsApi.removeDislike(postId, userId)
        }

    override suspend fun filters(): Filters =
        withContext(ioDispatcher) {
            postsApi.filters()
        }

    override suspend fun posts(): List<IdeaPost> = withContext(ioDispatcher) {
        postsApi.posts()
    }

    override suspend fun findPostById(postId: Long): IdeaPost? =
        withContext(ioDispatcher) {
            postsApi.findPostById(postId)
        }
}