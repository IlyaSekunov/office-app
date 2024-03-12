package ru.ilyasekunov.officeapp.data.repository.posts

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.ilyasekunov.officeapp.data.datasource.PostsDataSource
import ru.ilyasekunov.officeapp.data.datasource.remote.PostsPagingDataSource
import ru.ilyasekunov.officeapp.data.dto.FiltersDto
import ru.ilyasekunov.officeapp.data.dto.SearchDto
import ru.ilyasekunov.officeapp.data.model.IdeaPost

object PostsPagingDefaults {
    val PagingConfig = PagingConfig(
        pageSize = 10
    )
}

class PostsPagingRepository(private val postsDataSource: PostsDataSource) {
    fun posts(filtersDto: FiltersDto, searchDto: SearchDto): Flow<PagingData<IdeaPost>> {
        return Pager(config = PostsPagingDefaults.PagingConfig) {
            PostsPagingDataSource(filtersDto, searchDto, postsDataSource)
        }.flow
    }
}