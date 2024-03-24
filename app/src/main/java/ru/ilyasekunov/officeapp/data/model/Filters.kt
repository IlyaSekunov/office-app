package ru.ilyasekunov.officeapp.data.model

import com.google.gson.annotations.SerializedName

data class Filters(
    val offices: List<Office>,
    @SerializedName("sortingFilters")
    val sortingCategories: List<SortingCategory>
)

data class SortingCategory(
    val id: Int,
    val name: String
)