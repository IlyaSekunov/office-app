package ru.ilyasekunov.officeapp.data.datasource

import androidx.annotation.IntRange
import ru.ilyasekunov.officeapp.data.dto.EditPostDto
import ru.ilyasekunov.officeapp.data.dto.FiltersDto
import ru.ilyasekunov.officeapp.data.dto.PublishPostDto
import ru.ilyasekunov.officeapp.data.model.Filters
import ru.ilyasekunov.officeapp.data.model.IdeaPost

interface PostsDataSource {
    suspend fun publishPost(post: PublishPostDto): Result<Unit>
    suspend fun posts(): Result<List<IdeaPost>>
    suspend fun findPostById(postId: Long): Result<IdeaPost?>
    suspend fun posts(
        filtersDto: FiltersDto,
        @IntRange(from = 1)
        page: Int,
        @IntRange(from = 1, to = 50)
        pageSize: Int = 10
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