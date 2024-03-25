package ru.ilyasekunov.officeapp.data.model

data class Filters(
    val offices: List<Office>,
    val sortingCategories: List<SortingCategory>
)

data class SortingCategory(
    val id: Int,
    val name: String
)