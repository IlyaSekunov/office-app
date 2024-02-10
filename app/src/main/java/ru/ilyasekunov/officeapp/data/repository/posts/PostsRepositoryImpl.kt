package ru.ilyasekunov.officeapp.data.repository.posts

import ru.ilyasekunov.officeapp.data.datasource.PostsDatasource
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

    override suspend fun updatePost(post: IdeaPost) {
        postsDatasource.updatePost(post)
    }

    override suspend fun deletePostById(postId: Long) {
        postsDatasource.deletePostById(postId)
    }
}