package ru.ilyasekunov.officeapp.util

import androidx.compose.foundation.lazy.LazyListState
import kotlin.math.abs

fun LazyListState.findNearestToCenterOfScreenItem(): Int {
    val visibleItemsInfo = layoutInfo.visibleItemsInfo
    if (visibleItemsInfo.isEmpty()) return -1
    val center = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
    return visibleItemsInfo.minBy {
        val itemCenter = it.offset + it.size / 2
        abs(center - itemCenter)
    }.index
}