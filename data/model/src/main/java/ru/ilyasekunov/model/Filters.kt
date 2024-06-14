package ru.ilyasekunov.model

import com.google.gson.annotations.SerializedName

data class Filters(
    @SerializedName("offices")
    val offices: List<Office>,
    @SerializedName("sortingCategories")
    val sortingCategories: List<SortingCategory>
)

data class SortingCategory(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

enum class SortingCategories(val id: Int) {
    COMMENTS(1), LIKES(2), DISLIKES(3)
}