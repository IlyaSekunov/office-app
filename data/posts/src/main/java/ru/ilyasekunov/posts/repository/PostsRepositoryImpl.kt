package ru.ilyasekunov.posts.repository

import ru.ilyasekunov.dto.EditPostDto
import ru.ilyasekunov.dto.PublishPostDto
import ru.ilyasekunov.model.Filters
import ru.ilyasekunov.model.IdeaPost
import ru.ilyasekunov.posts.datasource.PostsDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PostsRepositoryImpl @Inject constructor(
    private val postsDatasource: PostsDataSource
) : PostsRepository {
    override suspend fun publishPost(post: PublishPostDto): Result<Unit> {
        return postsDatasource.publishPost(post)
    }

    override suspend fun editPostById(postId: Long, editedPostDto: EditPostDto): Result<Unit> {
        return postsDatasource.editPostById(postId, editedPostDto)
    }

    override suspend fun findPostById(postId: Long): Result<IdeaPost> {
        return postsDatasource.findPostById(postId)
    }

    override suspend fun deletePostById(postId: Long): Result<Unit> {
        return postsDatasource.deletePostById(postId)
    }

    override suspend fun pressLike(postId: Long): Result<Unit> {
        return postsDatasource.pressLike(postId)
    }

    override suspend fun removeLike(postId: Long): Result<Unit> {
        return postsDatasource.removeLike(postId)
    }

    override suspend fun pressDislike(postId: Long): Result<Unit> {
        return postsDatasource.pressDislike(postId)
    }

    override suspend fun removeDislike(postId: Long): Result<Unit> {
        return postsDatasource.removeDislike(postId)
    }

    override suspend fun filters(): Result<Filters> {
        return postsDatasource.filters()
    }

    override suspend fun suggestIdeaToMyOffice(postId: Long): Result<Unit> {
        return postsDatasource.suggestIdeaToMyOffice(postId)
    }
}