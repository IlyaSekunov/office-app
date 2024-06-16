package ru.ilyasekunov.author.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.ilyasekunov.model.IdeaAuthor

internal interface AuthorApi {
    @GET("users/authors/{authorId}")
    suspend fun ideaAuthorById(@Path("authorId") authorId: Long): Response<IdeaAuthor>

    @GET("users/my-office/employees")
    suspend fun officeEmployees(
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int
    ): Response<List<IdeaAuthor>>
}