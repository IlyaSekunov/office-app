package ru.ilyasekunov.officeapp.util

import androidx.paging.compose.LazyPagingItems

fun <T : Any> LazyPagingItems<T>.isEmpty() = itemCount == 0