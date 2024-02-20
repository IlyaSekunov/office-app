package ru.ilyasekunov.officeapp.data.repository.posts

import ru.ilyasekunov.officeapp.data.dto.EditPostDto
import ru.ilyasekunov.officeapp.data.dto.PublishPostDto
import ru.ilyasekunov.officeapp.data.model.IdeaPost

interface PostsRepository {
    suspend fun posts(): List<IdeaPost>
    suspend fun publishPost(post: PublishPostDto)
    suspend fun editPostById(postId: Long, editedPostDto: EditPostDto)
    suspend fun findPostById(postId: Long): IdeaPost?
    suspend fun deletePostById(postId: Long)
    suspend fun pressLike(postId: Long, userId: Long)
    suspend fun removeLike(postId: Long, userId: Long)
    suspend fun pressDislike(postId: Long, userId: Long)
    suspend fun removeDislike(postId: Long, userId: Long)
}