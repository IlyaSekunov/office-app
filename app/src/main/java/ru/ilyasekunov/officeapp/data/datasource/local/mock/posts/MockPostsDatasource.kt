package ru.ilyasekunov.officeapp.data.datasource.local.mock.posts

import ru.ilyasekunov.officeapp.data.datasource.PostsDatasource
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

    override suspend fun updatePost(post: IdeaPost) {
        val previousPost = posts.find { post.id == it.id }
        if (previousPost != null) {
            posts[posts.indexOf(previousPost)] = post
        }
    }

    override suspend fun deletePostById(postId: Long) {
        val post = posts.find { it.id == postId }
        if (post != null) {
            posts -= post
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