package ru.ilyasekunov.dto

data class FiltersDto(
    val offices: List<Int> = emptyList(),
    val sortingFilter: Int? = null
)