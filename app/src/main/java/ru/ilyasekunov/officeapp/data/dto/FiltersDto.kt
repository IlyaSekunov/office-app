package ru.ilyasekunov.officeapp.data.dto

data class FiltersDto(
    val offices: List<Int> = emptyList(),
    val sortingFilter: Int? = null
)