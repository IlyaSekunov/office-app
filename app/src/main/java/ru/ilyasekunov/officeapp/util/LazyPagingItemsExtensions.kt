package ru.ilyasekunov.officeapp.util

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

fun <T : Any> LazyPagingItems<T>.isEmpty() = itemCount == 0

fun <T : Any> LazyPagingItems<T>.isRefreshing() = loadState.refresh == LoadState.Loading

fun <T : Any> LazyPagingItems<T>.isError() = loadState.hasError

fun <T : Any> LazyPagingItems<T>.isAppending() = loadState.append == LoadState.Loading