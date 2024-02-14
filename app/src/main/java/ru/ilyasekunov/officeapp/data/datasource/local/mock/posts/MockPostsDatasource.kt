package ru.ilyasekunov.officeapp.data.datasource.local.mock.posts

import ru.ilyasekunov.officeapp.data.datasource.PostsDatasource
import ru.ilyasekunov.officeapp.data.dto.EditPostDto
import ru.ilyasekunov.officeapp.data.dto.PublishPostDto
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.preview.ideaPost
import java.time.LocalDateTime

class MockPostsDatasource : PostsDatasource {
    private val posts = mutableListOf(ideaPost)

    override suspend fun publishPost(post: PublishPostDto) {
        synchronized(this) {
            posts.add(post.toIdeaPost())
        }
    }

    override suspend fun findPosts(): List<IdeaPost> {
        return posts
    }

    override suspend fun findPostById(postId: Long): IdeaPost? {
        return posts.find { it.id == postId }
    }

    override suspend fun editPostById(postId: Long, editedPost: EditPostDto) {
        val post = posts.find { it.id == postId }
        post?.let {
            val updatedPost = it.copy(
                title = editedPost.title,
                content = editedPost.content,
                attachedImages = editedPost.attachedImages
            )
            posts[posts.indexOf(it)] = updatedPost
        }
    }

    override suspend fun deletePostById(postId: Long) {
        val post = posts.find { it.id == postId }
        post?.let {
            posts -= it
        }
    }

    override suspend fun pressLike(postId: Long, userId: Long) {
        val post = posts.find { it.id == postId }
        post?.let {
            posts[posts.indexOf(it)] = it.copy(
                isLikePressed = true,
                likesCount = it.likesCount + 1
            )
            if (post.isDislikePressed) {
                removeDislike(postId, userId)
            }
        }
    }

    override suspend fun removeLike(postId: Long, userId: Long) {
        val post = posts.find { it.id == postId }
        post?.let {
            posts[posts.indexOf(it)] = it.copy(
                isLikePressed = false,
                likesCount = it.likesCount - 1
            )
        }
    }

    override suspend fun pressDislike(postId: Long, userId: Long) {
        val post = posts.find { it.id == postId }
        post?.let {
            posts[posts.indexOf(it)] = it.copy(
                isDislikePressed = true,
                dislikesCount = it.dislikesCount + 1
            )
            if (post.isLikePressed) {
                removeLike(postId, userId)
            }
        }
    }

    override suspend fun removeDislike(postId: Long, userId: Long) {
        val post = posts.find { it.id == postId }
        post?.let {
            posts[posts.indexOf(it)] = it.copy(
                isDislikePressed = false,
                dislikesCount = it.dislikesCount - 1
            )
        }
    }

    private fun PublishPostDto.toIdeaPost(): IdeaPost =
        IdeaPost(
            id = if (posts.isNotEmpty()) posts.maxOf { it.id } + 1 else 0,
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