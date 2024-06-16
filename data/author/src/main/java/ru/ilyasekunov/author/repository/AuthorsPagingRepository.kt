package ru.ilyasekunov.author.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.ilyasekunov.model.IdeaAuthor

interface AuthorsPagingRepository {
    fun officeEmployees(): Flow<PagingData<IdeaAuthor>>
}