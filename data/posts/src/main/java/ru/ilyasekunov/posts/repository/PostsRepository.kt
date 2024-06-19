package ru.ilyasekunov.posts.repository

import ru.ilyasekunov.dto.EditPostDto
import ru.ilyasekunov.dto.PublishPostDto
import ru.ilyasekunov.model.Filters
import ru.ilyasekunov.model.IdeaPost

interface PostsRepository {
    suspend fun publishPost(post: PublishPostDto): Result<Unit>
    suspend fun editPostById(postId: Long, editedPostDto: EditPostDto): Result<Unit>
    suspend fun findPostById(postId: Long): Result<IdeaPost>
    suspend fun deletePostById(postId: Long): Result<Unit>
    suspend fun pressLike(postId: Long): Result<Unit>
    suspend fun removeLike(postId: Long): Result<Unit>
    suspend fun pressDislike(postId: Long): Result<Unit>
    suspend fun removeDislike(postId: Long): Result<Unit>
    suspend fun filters(): Result<Filters>
    suspend fun suggestIdeaToMyOffice(postId: Long): Result<Unit>
}