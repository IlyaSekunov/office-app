package ru.ilyasekunov.officeapp.data.datasource.local.mock

import ru.ilyasekunov.officeapp.data.datasource.PostsDataSource
import ru.ilyasekunov.officeapp.data.dto.EditPostDto
import ru.ilyasekunov.officeapp.data.dto.PublishPostDto
import ru.ilyasekunov.officeapp.data.dto.SearchPostsDto
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

    override suspend fun posts(): Result<List<IdeaPost>> = Result.success(Posts)
    override suspend fun posts(
        searchPostsDto: SearchPostsDto,
        page: Int,
        pageSize: Int
    ): Result<List<IdeaPost>> {
        val searchDto = searchPostsDto.searchDto
        val filtersDto = searchPostsDto.filtersDto
        val posts = Posts.filter {
            if (searchDto.search.isBlank()) {
                it.office.id in filtersDto.offices
            } else {
                it.office.id in filtersDto.offices &&
                        it.title.contains(searchDto.search)
            }
        }
        val sortedPosts = when (filtersDto.sortingFilter) {
            0 -> posts.sortedBy { it.likesCount }
            1 -> posts.sortedBy { it.dislikesCount }
            2 -> posts.sortedBy { it.commentsCount }
            null -> posts.sortedBy { it.date }
            else -> throw IllegalStateException("Unknown sorting filter")
        }
        val firstPostIndex = (page - 1) * pageSize
        val lastPostIndex = firstPostIndex + pageSize
        if (firstPostIndex > sortedPosts.lastIndex) {
            return Result.success(emptyList())
        }
        if (lastPostIndex > sortedPosts.lastIndex) {
            return Result.success(
                sortedPosts.subList(
                    fromIndex = firstPostIndex,
                    toIndex = sortedPosts.lastIndex + 1
                )
            )
        }
        return Result.success(
            sortedPosts.subList(
                fromIndex = firstPostIndex,
                toIndex = lastPostIndex
            )
        )
    }

    override suspend fun findPostById(postId: Long): Result<IdeaPost?> {
        return Result.success(Posts.find { it.id == postId })
    }

    override suspend fun postsByAuthorId(
        authorId: Long,
        page: Int,
        pageSize: Int
    ): Result<List<IdeaPost>> {
        val posts = Posts.filter { it.ideaAuthor.id == authorId }
        val firstPostIndex = (page - 1) * pageSize
        val lastPostIndex = firstPostIndex + pageSize
        if (firstPostIndex > posts.lastIndex) {
            return Result.success(emptyList())
        }
        if (lastPostIndex > posts.lastIndex) {
            return Result.success(
                posts.subList(
                    fromIndex = firstPostIndex,
                    toIndex = posts.lastIndex + 1
                )
            )
        }
        return Result.success(
            posts.subList(
                fromIndex = firstPostIndex,
                toIndex = lastPostIndex
            )
        )
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

    override suspend fun filters(): Result<Filters> =
        Result.success(
            Filters(
                offices = Offices,
                sortingCategories = SortingCategories
            )
        )

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
        photo = photo,
        office = office
    )