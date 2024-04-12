package ru.ilyasekunov.officeapp.data.datasource

import androidx.annotation.IntRange
import ru.ilyasekunov.officeapp.data.dto.EditPostDto
import ru.ilyasekunov.officeapp.data.dto.PublishPostDto
import ru.ilyasekunov.officeapp.data.dto.SearchPostsDto
import ru.ilyasekunov.officeapp.data.model.Filters
import ru.ilyasekunov.officeapp.data.model.IdeaPost

interface PostsDataSource {
    suspend fun publishPost(post: PublishPostDto): Result<Unit>
    suspend fun findPostById(postId: Long): Result<IdeaPost>
    suspend fun posts(
        searchPostsDto: SearchPostsDto,
        @IntRange(from = 1)
        page: Int,
        @IntRange(from = 1)
        pageSize: Int
    ): Result<List<IdeaPost>>

    suspend fun favouritePosts(): Result<List<IdeaPost>>

    suspend fun postsByAuthorId(
        authorId: Long,
        @IntRange(from = 1)
        page: Int,
        @IntRange(from = 1)
        pageSize: Int
    ): Result<List<IdeaPost>>

    suspend fun editPostById(
        postId: Long,
        editedPost: EditPostDto
    ): Result<Unit>

    suspend fun deletePostById(postId: Long): Result<Unit>
    suspend fun pressLike(postId: Long): Result<Unit>
    suspend fun removeLike(postId: Long): Result<Unit>
    suspend fun pressDislike(postId: Long): Result<Unit>
    suspend fun removeDislike(postId: Long): Result<Unit>
    suspend fun filters(): Result<Filters>
}