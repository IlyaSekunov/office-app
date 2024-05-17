package ru.ilyasekunov.officeapp.ui

import androidx.paging.PagingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PagingDataUiState<T : Any>(initialData: PagingData<T> = PagingData.empty()) {
    private val _data = MutableStateFlow(initialData)
    val data get() = _data.asStateFlow()

    fun updateData(data: PagingData<T>) {
        _data.value = data
    }
}