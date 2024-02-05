package ru.ilyasekunov.officeapp.data.repository.posts

import ru.ilyasekunov.officeapp.data.SortingFilter

class PostsRepositoryImpl : PostsRepository {
    override suspend fun findFilters(): List<SortingFilter> {
        return listOf(
            SortingFilter("Лайкам"),
            SortingFilter("Дизлайкам"),
            SortingFilter("Комментируемости"),
            SortingFilter("Дате")
        )
    }
}