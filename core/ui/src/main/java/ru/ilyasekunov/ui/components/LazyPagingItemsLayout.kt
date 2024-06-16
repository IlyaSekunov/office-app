package ru.ilyasekunov.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import ru.ilyasekunov.ui.LoadingScreen
import ru.ilyasekunov.ui.isAppending
import ru.ilyasekunov.ui.isEmpty
import ru.ilyasekunov.ui.isErrorWhileAppending
import ru.ilyasekunov.ui.isErrorWhileRefreshing
import ru.ilyasekunov.ui.isRefreshing

@Composable
fun <T : Any> LazyPagingItemsColumn(
    items: LazyPagingItems<T>,
    isPullToRefreshActive: Boolean,
    itemKey: ((item: @JvmSuppressWildcards T) -> Any)?,
    itemsEmptyComposable: @Composable () -> Unit,
    itemComposable: @Composable (T) -> Unit,
    errorWhileRefreshComposable: @Composable () -> Unit,
    errorWhileAppendComposable: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    refreshIndicatorStrokeWidth: Dp = 3.dp,
    refreshIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    refreshIndicatorSize: Dp = 40.dp,
    appendIndicatorStrokeWidth: Dp = 3.dp,
    appendIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    appendIndicatorSize: Dp = 24.dp
) {
    when {
        items.isErrorWhileRefreshing() -> errorWhileRefreshComposable()
        items.isRefreshing() && !isPullToRefreshActive -> {
            LoadingScreen(
                indicatorColor = refreshIndicatorColor,
                indicatorStrokeWidth = refreshIndicatorStrokeWidth,
                indicatorSize = refreshIndicatorSize,
                modifier = Modifier.padding(20.dp)
            )
        }

        items.isEmpty() -> itemsEmptyComposable()
        else -> {
            LazyColumn(
                state = lazyListState,
                contentPadding = contentPadding,
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
                modifier = modifier
            ) {
                items(
                    count = items.itemCount,
                    key = items.itemKey(key = itemKey)
                ) {
                    val item = items[it]!!
                    itemComposable(item)
                }
                if (items.isAppending()) {
                    item {
                        LoadingScreen(
                            indicatorStrokeWidth = appendIndicatorStrokeWidth,
                            indicatorColor = appendIndicatorColor,
                            indicatorSize = appendIndicatorSize,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
                if (items.isErrorWhileAppending()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        ) {
                            errorWhileAppendComposable()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun <T : Any> LazyPagingItemsHorizontalGrid(
    items: LazyPagingItems<T>,
    isPullToRefreshActive: Boolean,
    rows: GridCells,
    itemKey: ((item: @JvmSuppressWildcards T) -> Any)?,
    itemsEmptyComposable: @Composable () -> Unit,
    itemComposable: @Composable (T) -> Unit,
    errorWhileRefreshComposable: @Composable () -> Unit,
    errorWhileAppendComposable: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    refreshIndicatorStrokeWidth: Dp = 3.dp,
    refreshIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    refreshIndicatorSize: Dp = 30.dp,
    appendIndicatorStrokeWidth: Dp = 3.dp,
    appendIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    appendIndicatorSize: Dp = 24.dp
) {
    when {
        items.isErrorWhileRefreshing() -> errorWhileRefreshComposable()
        items.isRefreshing() && !isPullToRefreshActive -> {
            LoadingScreen(
                indicatorColor = refreshIndicatorColor,
                indicatorStrokeWidth = refreshIndicatorStrokeWidth,
                indicatorSize = refreshIndicatorSize
            )
        }

        items.isEmpty() -> itemsEmptyComposable()
        else -> {
            LazyHorizontalGrid(
                rows = rows,
                contentPadding = contentPadding,
                verticalArrangement = verticalArrangement,
                horizontalArrangement = horizontalArrangement,
                modifier = modifier
            ) {
                items(
                    count = items.itemCount,
                    key = items.itemKey(key = itemKey)
                ) {
                    val item = items[it]!!
                    itemComposable(item)
                }
                if (items.isAppending()) {
                    item {
                        LoadingScreen(
                            indicatorStrokeWidth = appendIndicatorStrokeWidth,
                            indicatorColor = appendIndicatorColor,
                            indicatorSize = appendIndicatorSize,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
                if (items.isErrorWhileAppending()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        ) {
                            errorWhileAppendComposable()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun <T : Any> LazyPagingItemsVerticalGrid(
    items: LazyPagingItems<T>,
    isPullToRefreshActive: Boolean,
    columns: GridCells,
    itemKey: ((item: @JvmSuppressWildcards T) -> Any)?,
    itemsEmptyComposable: @Composable () -> Unit,
    itemsAppendComposable: @Composable () -> Unit,
    itemComposable: @Composable (T) -> Unit,
    errorWhileRefreshComposable: @Composable () -> Unit,
    errorWhileAppendComposable: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    refreshIndicatorStrokeWidth: Dp = 3.dp,
    refreshIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    refreshIndicatorSize: Dp = 40.dp
) {
    when {
        items.isErrorWhileRefreshing() -> errorWhileRefreshComposable()
        items.isRefreshing() && !isPullToRefreshActive -> {
            LoadingScreen(
                indicatorColor = refreshIndicatorColor,
                indicatorStrokeWidth = refreshIndicatorStrokeWidth,
                indicatorSize = refreshIndicatorSize
            )
        }

        items.isEmpty() -> itemsEmptyComposable()
        else -> {
            LazyVerticalGrid(
                state = lazyGridState,
                columns = columns,
                contentPadding = contentPadding,
                verticalArrangement = verticalArrangement,
                horizontalArrangement = horizontalArrangement,
                modifier = modifier
            ) {
                items(
                    count = items.itemCount,
                    key = items.itemKey(key = itemKey)
                ) {
                    val item = items[it]!!
                    itemComposable(item)
                }
                if (items.isAppending()) {
                    item { itemsAppendComposable() }
                }
                if (items.isErrorWhileAppending()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        ) {
                            errorWhileAppendComposable()
                        }
                    }
                }
            }
        }
    }
}