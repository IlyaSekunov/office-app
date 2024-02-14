package ru.ilyasekunov.officeapp.data.repository.posts

import ru.ilyasekunov.officeapp.data.datasource.PostsDatasource
import ru.ilyasekunov.officeapp.data.dto.EditPostDto
import ru.ilyasekunov.officeapp.data.dto.PublishPostDto
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import javax.inject.Inject

class PostsRepositoryImpl @Inject constructor(
    private val postsDatasource: PostsDatasource
) : PostsRepository {
    override suspend fun findPosts(): List<IdeaPost> {
        return postsDatasource.findPosts()
    }

    override suspend fun publishPost(post: PublishPostDto) {
        postsDatasource.publishPost(post)
    }

    override suspend fun editPostById(postId: Long, editedPostDto: EditPostDto) {
        postsDatasource.editPostById(postId, editedPostDto)
    }

    override suspend fun findPostById(postId: Long): IdeaPost? {
        return postsDatasource.findPostById(postId)
    }

    override suspend fun deletePostById(postId: Long) {
        postsDatasource.deletePostById(postId)
    }

    override suspend fun pressLike(postId: Long, userId: Long) {
        postsDatasource.pressLike(postId, userId)
    }

    override suspend fun removeLike(postId: Long, userId: Long) {
        postsDatasource.removeLike(postId, userId)
    }

    override suspend fun pressDislike(postId: Long, userId: Long) {
        postsDatasource.pressDislike(postId, userId)
    }

    override suspend fun removeDislike(postId: Long, userId: Long) {
        postsDatasource.removeDislike(postId, userId)
    }
}