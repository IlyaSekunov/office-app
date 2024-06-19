package ru.ilyasekunov.dto

data class SearchPostsDto(
    val filtersDto: FiltersDto,
    val searchDto: SearchDto
)