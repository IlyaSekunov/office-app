package ru.ilyasekunov.officeapp.data.repository.posts

import ru.ilyasekunov.officeapp.data.datasource.PostsDataSource
import ru.ilyasekunov.officeapp.data.dto.EditPostDto
import ru.ilyasekunov.officeapp.data.dto.PublishPostDto
import ru.ilyasekunov.officeapp.data.dto.SearchPostsDto
import ru.ilyasekunov.officeapp.data.model.Filters
import ru.ilyasekunov.officeapp.data.model.IdeaPost

class PostsRepositoryImpl(
    private val postsDatasource: PostsDataSource
) : PostsRepository {
    override suspend fun posts(
        searchPostsDto: SearchPostsDto,
        page: Int,
        pageSize: Int
    ): Result<List<IdeaPost>> {
        return postsDatasource.posts(searchPostsDto, page, pageSize)
    }

    override suspend fun favouritePosts(
        searchPostsDto: SearchPostsDto,
        page: Int,
        pageSize: Int
    ): Result<List<IdeaPost>> {
        return postsDatasource.favouritePosts(searchPostsDto, page, pageSize)
    }

    override suspend fun suggestedIdeas(page: Int, pageSize: Int): Result<List<IdeaPost>> {
        return postsDatasource.suggestedIdeas(page, pageSize)
    }

    override suspend fun ideasInProgress(page: Int, pageSize: Int): Result<List<IdeaPost>> {
        return postsDatasource.ideasInProgress(page, pageSize)
    }

    override suspend fun implementedIdeas(page: Int, pageSize: Int): Result<List<IdeaPost>> {
        return postsDatasource.implementedIdeas(page, pageSize)
    }

    override suspend fun publishPost(post: PublishPostDto): Result<Unit> {
        return postsDatasource.publishPost(post)
    }

    override suspend fun editPostById(postId: Long, editedPostDto: EditPostDto): Result<Unit> {
        return postsDatasource.editPostById(postId, editedPostDto)
    }

    override suspend fun findPostById(postId: Long): Result<IdeaPost> {
        return postsDatasource.findPostById(postId)
    }

    override suspend fun deletePostById(postId: Long): Result<Unit> {
        return postsDatasource.deletePostById(postId)
    }

    override suspend fun pressLike(postId: Long): Result<Unit> {
        return postsDatasource.pressLike(postId)
    }

    override suspend fun removeLike(postId: Long): Result<Unit> {
        return postsDatasource.removeLike(postId)
    }

    override suspend fun pressDislike(postId: Long): Result<Unit> {
        return postsDatasource.pressDislike(postId)
    }

    override suspend fun removeDislike(postId: Long): Result<Unit> {
        return postsDatasource.removeDislike(postId)
    }

    override suspend fun filters(): Result<Filters> {
        return postsDatasource.filters()
    }

    override suspend fun suggestIdeaToMyOffice(postId: Long): Result<Unit> {
        return postsDatasource.suggestIdeaToMyOffice(postId)
    }
}