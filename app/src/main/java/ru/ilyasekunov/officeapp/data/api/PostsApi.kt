package ru.ilyasekunov.officeapp.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.ilyasekunov.officeapp.data.dto.EditPostDto
import ru.ilyasekunov.officeapp.data.dto.PublishPostDto
import ru.ilyasekunov.officeapp.data.model.Filters
import ru.ilyasekunov.officeapp.data.model.IdeaPost

interface PostsApi {
    @POST("posts")
    suspend fun publishPost(@Body post: PublishPostDto): Response<Unit>

    @GET("posts")
    suspend fun posts(
        @Query("office") officesId: List<Int>,
        @Query("sorting_filter") sortingFilterId: Int? = null,
        @Query("search") search: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int
    ): Response<List<IdeaPost>>

    @GET("posts/by-author-id/{authorId}")
    suspend fun postsByAuthorId(
        @Path("authorId") authorId: Long,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int
    ): Response<List<IdeaPost>>

    @GET("posts/{postId}")
    suspend fun findPostById(@Path("postId") postId: Long): Response<IdeaPost?>

    @PATCH("posts/{postId}")
    suspend fun editPostById(
        @Path("postId") postId: Long,
        @Body editedPost: EditPostDto
    ): Response<Unit>

    @DELETE("posts/{postId}")
    suspend fun deletePostById(@Path("postId") postId: Long): Response<Unit>

    @POST("posts/{postId}/like")
    suspend fun pressLike(@Path("postId") postId: Long): Response<Unit>

    @DELETE("posts/{postId}/like")
    suspend fun removeLike(@Path("postId") postId: Long): Response<Unit>

    @POST("posts/{postId}/dislike")
    suspend fun pressDislike(@Path("postId") postId: Long): Response<Unit>

    @DELETE("posts/{postId}/dislike")
    suspend fun removeDislike(@Path("postId") postId: Long): Response<Unit>

    @GET("posts/filters")
    suspend fun filters(): Response<Filters>

    suspend fun posts(): Response<List<IdeaPost>>
}