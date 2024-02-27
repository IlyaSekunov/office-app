package ru.ilyasekunov.officeapp.data.datasource.local.mock

import ru.ilyasekunov.officeapp.data.datasource.PostsDataSource
import ru.ilyasekunov.officeapp.data.dto.EditPostDto
import ru.ilyasekunov.officeapp.data.dto.FiltersDto
import ru.ilyasekunov.officeapp.data.dto.PublishPostDto
import ru.ilyasekunov.officeapp.data.model.Filters
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import java.time.LocalDateTime

class MockPostsDataSource : PostsDataSource {
    override suspend fun publishPost(post: PublishPostDto) {
        synchronized(Posts) {
            Posts += post.toIdeaPost()
        }
    }

    override suspend fun posts(): List<IdeaPost> = Posts


    override suspend fun posts(filtersDto: FiltersDto, page: Int, pageSize: Int): List<IdeaPost> {
        TODO("Not yet implemented")
    }

    override suspend fun findPostById(postId: Long): IdeaPost? {
        return Posts.find { it.id == postId }
    }

    override suspend fun editPostById(postId: Long, editedPost: EditPostDto) {
        val post = Posts.find { it.id == postId }
        post?.let {
            val updatedPost = it.copy(
                title = editedPost.title,
                content = editedPost.content,
                attachedImages = editedPost.attachedImages
            )
            Posts[Posts.indexOf(it)] = updatedPost
        }
    }

    override suspend fun deletePostById(postId: Long) {
        val post = Posts.find { it.id == postId }
        post?.let {
            Posts -= it
        }
    }

    override suspend fun pressLike(postId: Long, userId: Long) {
        val post = Posts.find { it.id == postId }
        post?.let {
            Posts[Posts.indexOf(it)] = it.copy(
                isLikePressed = true,
                likesCount = it.likesCount + 1
            )
            if (post.isDislikePressed) {
                removeDislike(postId, userId)
            }
        }
    }

    override suspend fun removeLike(postId: Long, userId: Long) {
        val post = Posts.find { it.id == postId }
        post?.let {
            Posts[Posts.indexOf(it)] = it.copy(
                isLikePressed = false,
                likesCount = it.likesCount - 1
            )
        }
    }

    override suspend fun pressDislike(postId: Long, userId: Long) {
        val post = Posts.find { it.id == postId }
        post?.let {
            Posts[Posts.indexOf(it)] = it.copy(
                isDislikePressed = true,
                dislikesCount = it.dislikesCount + 1
            )
            if (post.isLikePressed) {
                removeLike(postId, userId)
            }
        }
    }

    override suspend fun removeDislike(postId: Long, userId: Long) {
        val post = Posts.find { it.id == postId }
        post?.let {
            Posts[Posts.indexOf(it)] = it.copy(
                isDislikePressed = false,
                dislikesCount = it.dislikesCount - 1
            )
        }
    }

    override suspend fun filters(): Filters {
        return Filters(
            offices = Offices,
            sortingCategories = SortingCategories
        )
    }

    private fun PublishPostDto.toIdeaPost(): IdeaPost =
        IdeaPost(
            id = if (Posts.isNotEmpty()) Posts.maxOf { it.id } + 1 else 0,
            title = title,
            content = content,
            date = LocalDateTime.now(),
            ideaAuthor = author,
            attachedImages = attachedImages,
            office = office,
            likesCount = 0,
            dislikesCount = 0,
            commentsCount = 0,
            isLikePressed = false,
            isDislikePressed = false
        )
}