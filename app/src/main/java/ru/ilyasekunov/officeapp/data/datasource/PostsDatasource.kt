package ru.ilyasekunov.officeapp.data.datasource

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.ilyasekunov.officeapp.data.dto.EditPostDto
import ru.ilyasekunov.officeapp.data.dto.FiltersDto
import ru.ilyasekunov.officeapp.data.dto.PublishPostDto
import ru.ilyasekunov.officeapp.data.model.IdeaPost

interface PostsDatasource {
    @POST("posts")
    suspend fun publishPost(@Body post: PublishPostDto)
    suspend fun posts(): List<IdeaPost>
    suspend fun findPostById(postId: Long): IdeaPost?

    @GET("posts")
    suspend fun posts(
        @Body filtersDto: FiltersDto,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int = 10
    ): List<IdeaPost>

    @PATCH("posts/{postId}")
    suspend fun editPostById(
        @Path("postId") postId: Long,
        @Body editedPost: EditPostDto
    )

    @DELETE("posts/{postId}")
    suspend fun deletePostById(@Path("postId") postId: Long)

    @POST("posts/{postId}/like")
    suspend fun pressLike(@Path("postId") postId: Long, userId: Long)

    @DELETE("posts/{postId}/like")
    suspend fun removeLike(@Path("postId") postId: Long, userId: Long)

    @POST("posts/{postId}/dislike")
    suspend fun pressDislike(@Path("postId") postId: Long, userId: Long)

    @DELETE("posts/{postId}/dislike")
    suspend fun removeDislike(@Path("postId") postId: Long, userId: Long)
}