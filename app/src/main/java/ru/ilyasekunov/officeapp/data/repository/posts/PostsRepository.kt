package ru.ilyasekunov.officeapp.data.repository.posts

import ru.ilyasekunov.officeapp.data.SortingFilter

interface PostsRepository {
    suspend fun findFilters(): List<SortingFilter>
}