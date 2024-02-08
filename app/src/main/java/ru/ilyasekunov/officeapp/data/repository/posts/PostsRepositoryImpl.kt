package ru.ilyasekunov.officeapp.data.repository.posts

import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.preview.ideaPost

class PostsRepositoryImpl : PostsRepository {
    override suspend fun findPosts(): List<IdeaPost> {
        return listOf(ideaPost)
    }
}