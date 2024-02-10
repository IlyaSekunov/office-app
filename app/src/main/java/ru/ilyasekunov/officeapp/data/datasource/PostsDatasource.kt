package ru.ilyasekunov.officeapp.data.datasource

import ru.ilyasekunov.officeapp.data.dto.PublishPostDto
import ru.ilyasekunov.officeapp.data.model.IdeaPost

interface PostsDatasource {
    suspend fun publishPost(post: PublishPostDto)
    suspend fun findPosts(): List<IdeaPost>
    suspend fun updatePost(post: IdeaPost)
    suspend fun deletePostById(postId: Long)
}