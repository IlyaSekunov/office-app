package ru.ilyasekunov.officeapp.data.dto

data class FiltersDto(
    val offices: List<Int>,
    val sortingFilter: Int
) {
    companion object {
        val Default = FiltersDto(
            offices = emptyList(),
            sortingFilter = 0
        )
    }
}