package ru.ilyasekunov.officeapp.data.datasource.local.mock

import ru.ilyasekunov.officeapp.data.datasource.PostsDataSource
import ru.ilyasekunov.officeapp.data.dto.EditPostDto
import ru.ilyasekunov.officeapp.data.dto.FiltersDto
import ru.ilyasekunov.officeapp.data.dto.PublishPostDto
import ru.ilyasekunov.officeapp.data.model.Filters
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.model.User
import java.time.LocalDateTime

class MockPostsDataSource : PostsDataSource {
    override suspend fun publishPost(post: PublishPostDto): Result<Unit> {
        synchronized(Posts) {
            Posts += post.toIdeaPost()
        }
        return Result.success(Unit)
    }

    override suspend fun posts(): Result<List<IdeaPost>> = Result.failure(Exception())


    override suspend fun posts(
        filtersDto: FiltersDto,
        page: Int,
        pageSize: Int
    ): Result<List<IdeaPost>> {
        TODO("Not yet implemented")
    }

    override suspend fun findPostById(postId: Long): Result<IdeaPost?> {
        return Result.success(Posts.find { it.id == postId })
    }

    override suspend fun editPostById(postId: Long, editedPost: EditPostDto): Result<Unit> {
        val post = Posts.find { it.id == postId }
        post?.let {
            val updatedPost = it.copy(
                title = editedPost.title,
                content = editedPost.content,
                attachedImages = editedPost.attachedImages
            )
            Posts[Posts.indexOf(it)] = updatedPost
        }
        return Result.success(Unit)
    }

    override suspend fun deletePostById(postId: Long): Result<Unit> {
        val post = Posts.find { it.id == postId }
        post?.let {
            Posts -= it
        }
        return Result.success(Unit)
    }

    override suspend fun pressLike(postId: Long): Result<Unit> {
        val post = Posts.find { it.id == postId }
        post?.let {
            Posts[Posts.indexOf(it)] = it.copy(
                isLikePressed = true,
                likesCount = it.likesCount + 1
            )
            if (post.isDislikePressed) {
                removeDislike(postId)
            }
        }
        return Result.success(Unit)
    }

    override suspend fun removeLike(postId: Long): Result<Unit> {
        val post = Posts.find { it.id == postId }
        post?.let {
            Posts[Posts.indexOf(it)] = it.copy(
                isLikePressed = false,
                likesCount = it.likesCount - 1
            )
        }
        return Result.success(Unit)
    }

    override suspend fun pressDislike(postId: Long): Result<Unit> {
        val post = Posts.find { it.id == postId }
        post?.let {
            Posts[Posts.indexOf(it)] = it.copy(
                isDislikePressed = true,
                dislikesCount = it.dislikesCount + 1
            )
            if (post.isLikePressed) {
                removeLike(postId)
            }
        }
        return Result.success(Unit)
    }

    override suspend fun removeDislike(postId: Long): Result<Unit> {
        val post = Posts.find { it.id == postId }
        post?.let {
            Posts[Posts.indexOf(it)] = it.copy(
                isDislikePressed = false,
                dislikesCount = it.dislikesCount - 1
            )
        }
        return Result.success(Unit)
    }

    override suspend fun filters(): Result<Filters> {
        return Result.success(
            Filters(
                offices = Offices,
                sortingCategories = SortingCategories
            )
        )
    }

    private fun PublishPostDto.toIdeaPost(): IdeaPost =
        IdeaPost(
            id = if (Posts.isNotEmpty()) Posts.maxOf { it.id } + 1 else 0,
            title = title,
            content = content,
            date = LocalDateTime.now(),
            ideaAuthor = User!!.toIdeaAuthor(),
            attachedImages = attachedImages,
            office = User!!.office,
            likesCount = 0,
            dislikesCount = 0,
            commentsCount = 0,
            isLikePressed = false,
            isDislikePressed = false
        )
}

fun User.toIdeaAuthor(): IdeaAuthor =
    IdeaAuthor(
        id = id,
        name = name,
        surname = surname,
        job = job,
        photo = photo
    )