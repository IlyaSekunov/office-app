package ru.ilyasekunov.officeapp.data.datasource

import ru.ilyasekunov.officeapp.data.dto.EditPostDto
import ru.ilyasekunov.officeapp.data.dto.PublishPostDto
import ru.ilyasekunov.officeapp.data.model.IdeaPost

interface PostsDatasource {
    suspend fun publishPost(post: PublishPostDto)
    suspend fun findPosts(): List<IdeaPost>
    suspend fun findPostById(postId: Long): IdeaPost?
    suspend fun editPostById(postId: Long, editedPost: EditPostDto)
    suspend fun deletePostById(postId: Long)
    suspend fun pressLike(postId: Long, userId: Long)
    suspend fun removeLike(postId: Long, userId: Long)
    suspend fun pressDislike(postId: Long, userId: Long)
    suspend fun removeDislike(postId: Long, userId: Long)
}