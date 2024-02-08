package ru.ilyasekunov.officeapp.data.repository.posts

import ru.ilyasekunov.officeapp.data.model.IdeaPost

interface PostsRepository {
    suspend fun findPosts(): List<IdeaPost>
}