package ru.ilyasekunov.officeapp.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.ilyasekunov.officeapp.data.dto.CommentDto
import ru.ilyasekunov.officeapp.data.model.Comment

interface CommentsApi {
    @GET("posts/{postId}/comments")
    suspend fun commentsByPostId(
        @Path("postId") postId: Long,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int
    ): Response<List<Comment>>

    @POST("posts/{postId}/comments/{commentId}/like")
    suspend fun pressLike(
        @Path("postId") postId: Long,
        @Path("commentId") commentId: Long
    ): Response<Unit>

    @DELETE("posts/{postId}/comments/{commentId}/like")
    suspend fun removeLike(
        @Path("postId") postId: Long,
        @Path("commentId") commentId: Long
    ): Response<Unit>

    @POST("posts/{postId}/comments/{commentId}/dislike")
    suspend fun pressDislike(
        @Path("postId") postId: Long,
        @Path("commentId") commentId: Long
    ): Response<Unit>

    @DELETE("posts/{postId}/comments/{commentId}/dislike")
    suspend fun removeDislike(
        @Path("postId") postId: Long,
        @Path("commentId") commentId: Long
    ): Response<Unit>

    @POST("posts/{postId}/comments")
    suspend fun sendComment(
        @Path("postId") postId: Long,
        @Body commentDto: CommentDto
    ): Response<Unit>
}