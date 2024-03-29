package ru.ilyasekunov.officeapp.data.model

data class Filters(
    val offices: List<Office>,
    val sortingCategories: List<SortingCategory>
)

data class SortingCategory(
    val id: Int,
    val name: String
)

enum class SortingCategories(val id: Int) {
    COMMENTS(1), LIKES(2), DISLIKES(3)
}