package ru.ilyasekunov.officeapp.data

data class SortingFilter(
    val id: Int,
    val name: String
)

val sortingFilters = listOf(
    SortingFilter(id = 0, name = "Лайкам"),
    SortingFilter(id = 1, name = "Дизлайкам"),
    SortingFilter(id = 2, name = "Комментариям")
)

val defaultSortFilter = SortingFilter(id = 4, name = "Дате")