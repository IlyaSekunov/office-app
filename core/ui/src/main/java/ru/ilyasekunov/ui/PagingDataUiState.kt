package ru.ilyasekunov.ui

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PagingDataUiState<T : Any>(initialData: PagingData<T> = PagingData.empty()) {
    private val _data = MutableStateFlow(initialData)
    val data get() = _data.asStateFlow()

    fun updateData(data: PagingData<T>) {
        _data.value = data
    }

    fun updateEntity(newEntity: T, condition: (T) -> Boolean) {
        _data.update { pagingData ->
            pagingData.map {
                if (condition(it)) newEntity
                else it
            }
        }
    }
}