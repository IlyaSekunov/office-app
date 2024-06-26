package ru.ilyasekunov.posts.datasource

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.ilyasekunov.dto.EditPostDto
import ru.ilyasekunov.dto.PublishPostDto
import ru.ilyasekunov.dto.SearchPostsDto
import ru.ilyasekunov.model.Filters
import ru.ilyasekunov.model.IdeaAuthor
import ru.ilyasekunov.model.IdeaPost
import ru.ilyasekunov.model.User
import ru.ilyasekunov.network.exceptions.HttpNotFoundException
import ru.ilyasekunov.test.offices
import ru.ilyasekunov.test.sortingCategories
import ru.ilyasekunov.test.user
import java.time.LocalDateTime
import javax.inject.Inject
import ru.ilyasekunov.test.posts as Posts

internal class FakePostsDataSource @Inject constructor() : PostsDataSource {
    private val lock = Mutex()

    override suspend fun publishPost(post: PublishPostDto): Result<Unit> {
        delay(1200L)
        
        lock.withLock { Posts += post.toIdeaPost() }

        return Result.success(Unit)
    }

    override suspend fun posts(
        searchPostsDto: SearchPostsDto,
        page: Int,
        pageSize: Int
    ): Result<List<IdeaPost>> {
        delay(1200L)

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
            1 -> posts.sortedBy { it.likesCount }
            2 -> posts.sortedBy { it.dislikesCount }
            3 -> posts.sortedBy { it.commentsCount }
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

    override suspend fun favouritePosts(
        searchPostsDto: SearchPostsDto,
        page: Int,
        pageSize: Int
    ): Result<List<IdeaPost>> {
        delay(1200L)

        val searchDto = searchPostsDto.searchDto
        val filtersDto = searchPostsDto.filtersDto

        val posts = Posts.asSequence()
            .filter {
                if (searchDto.search.isBlank()) {
                    it.office.id in filtersDto.offices
                } else {
                    it.office.id in filtersDto.offices &&
                            it.title.contains(searchDto.search)
                }
            }.filter { it.isLikePressed }
            .toList()

        val sortedPosts = when (filtersDto.sortingFilter) {
            1 -> posts.sortedBy { it.likesCount }
            2 -> posts.sortedBy { it.dislikesCount }
            3 -> posts.sortedBy { it.commentsCount }
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

    override suspend fun findPostById(postId: Long): Result<IdeaPost> {
        delay(1200L)

        val post = Posts.find { it.id == postId }

        return post?.let {
            Result.success(it)
        } ?: Result.failure(HttpNotFoundException())
    }

    override suspend fun postsByAuthorId(
        authorId: Long,
        page: Int,
        pageSize: Int
    ): Result<List<IdeaPost>> {
        delay(1200L)

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

    override suspend fun suggestedIdeas(page: Int, pageSize: Int): Result<List<IdeaPost>> {
        delay(1200L)

        val posts = Posts.filter { !it.isInProgress && !it.isImplemented }
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

    override suspend fun ideasInProgress(page: Int, pageSize: Int): Result<List<IdeaPost>> {
        delay(1200L)

        val posts = Posts.filter { it.isInProgress }
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

    override suspend fun implementedIdeas(page: Int, pageSize: Int): Result<List<IdeaPost>> {
        delay(1200L)

        val posts = Posts.filter { it.isImplemented }
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

    override suspend fun myIdeas(page: Int, pageSize: Int): Result<List<IdeaPost>> {
        delay(1200L)

        val posts = Posts.filter { it.ideaAuthor.id == user?.id }
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
        delay(1200L)

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
        delay(1200L)

        val post = Posts.find { it.id == postId }
        post?.let {
            Posts -= it
        }

        return Result.success(Unit)
    }

    override suspend fun pressLike(postId: Long): Result<Unit> {
        val post = Posts.find { it.id == postId }
        post?.let {
            lock.withLock {
                Posts[Posts.indexOf(it)] = it.copy(
                    isLikePressed = true,
                    likesCount = it.likesCount + 1
                )
            }
            if (post.isDislikePressed) {
                removeDislike(postId)
            }
        }
        return Result.success(Unit)
    }

    override suspend fun removeLike(postId: Long): Result<Unit> {
        val post = Posts.find { it.id == postId }

        post?.let {
            lock.withLock {
                Posts[Posts.indexOf(it)] = it.copy(
                    isLikePressed = false,
                    likesCount = it.likesCount - 1
                )
            }
        }

        return Result.success(Unit)
    }

    override suspend fun pressDislike(postId: Long): Result<Unit> {
        val post = Posts.find { it.id == postId }

        post?.let {
            lock.withLock {
                Posts[Posts.indexOf(it)] = it.copy(
                    isDislikePressed = true,
                    dislikesCount = it.dislikesCount + 1
                )
            }
            if (post.isLikePressed) {
                removeLike(postId)
            }
        }

        return Result.success(Unit)
    }

    override suspend fun removeDislike(postId: Long): Result<Unit> {
        val post = Posts.find { it.id == postId }

        post?.let {
            lock.withLock {
                Posts[Posts.indexOf(it)] = it.copy(
                    isDislikePressed = false,
                    dislikesCount = it.dislikesCount - 1
                )
            }
        }

        return Result.success(Unit)
    }

    override suspend fun filters(): Result<Filters> {
        delay(1200L)
        return Result.success(
            Filters(
                offices = offices,
                sortingCategories = sortingCategories
            )
        )
    }

    override suspend fun suggestIdeaToMyOffice(postId: Long): Result<Unit> {
        delay(1200L)
        return Result.success(Unit)
    }

    private fun PublishPostDto.toIdeaPost(): IdeaPost =
        IdeaPost(
            id = if (Posts.isNotEmpty()) Posts.maxOf { it.id } + 1 else 0,
            title = title,
            content = content,
            date = LocalDateTime.now(),
            ideaAuthor = user!!.toIdeaAuthor(),
            attachedImages = attachedImages,
            office = user!!.office,
            likesCount = 0,
            dislikesCount = 0,
            commentsCount = 0,
            isLikePressed = false,
            isDislikePressed = false,
            isImplemented = false,
            isInProgress = false,
            isSuggestedToMyOffice = true
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