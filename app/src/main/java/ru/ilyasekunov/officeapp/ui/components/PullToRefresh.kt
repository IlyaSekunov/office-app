package ru.ilyasekunov.officeapp.ui.components

import androidx.compose.animation.core.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.pow

private const val DragMultiplier = 0.5f
private val SpinnerContainerSize = 40.dp
private val Elevation = 3.0.dp

@OptIn(ExperimentalMaterial3Api::class)
class DownsidePullToRefreshState(
    initialRefreshing: Boolean,
    override val positionalThreshold: Float,
    enabled: () -> Boolean,
    private val coroutineScope: CoroutineScope
) : PullToRefreshState {
    override val progress get() = adjustedDistancePulled / positionalThreshold
    override val verticalOffset get() = _verticalOffset
    override val isRefreshing get() = _refreshing

    override fun startRefresh() {
        _refreshing = true
        _verticalOffset = -positionalThreshold
    }

    override fun endRefresh() {
        coroutineScope.launch { animateTo(0f) }
        _refreshing = false
    }

    override var nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(
            available: Offset,
            source: NestedScrollSource,
        ): Offset = when {
            !enabled() -> Offset.Zero
            // Screen is going to be scrolled up -> y > 0
            source == NestedScrollSource.UserInput && available.y > 0 -> {
                consumeAvailableOffset(available)
            }

            else -> Offset.Zero
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset = when {
            !enabled() -> Offset.Zero
            // Screen is going to be scrolled down -> y < 0
            source == NestedScrollSource.UserInput && available.y < 0 -> {
                consumeAvailableOffset(available)
            }

            else -> Offset.Zero
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            return Velocity(0f, onRelease(available.y))
        }
    }

    fun consumeAvailableOffset(available: Offset): Offset {
        val y = if (isRefreshing) 0f else {
            val newOffset = (-distancePulled + available.y).coerceAtMost(0f)
            val dragConsumed = -(newOffset + distancePulled)
            distancePulled = abs(newOffset)
            _verticalOffset = -calculateVerticalOffset()
            abs(dragConsumed)
        }
        return Offset(0f, y)
    }

    /** Helper method for nested scroll connection. Calls onRefresh callback when triggered */
    suspend fun onRelease(velocity: Float): Float {
        if (isRefreshing) return 0f // Already refreshing, do nothing
        // Trigger refresh
        if (adjustedDistancePulled > positionalThreshold) {
            startRefresh()
        } else {
            animateTo(0f)
        }

        val consumed = when {
            // We are flinging without having dragged the pull refresh (for example a fling inside
            // a list) - don't consume
            distancePulled == 0f -> 0f
            // If the velocity is negative, the fling is upwards, and we don't want to prevent the
            // the list from scrolling
            velocity < 0f -> 0f
            // We are showing the indicator, and the fling is downwards - consume everything
            else -> velocity
        }
        distancePulled = 0f
        return consumed
    }

    private suspend fun animateTo(offset: Float) {
        animate(initialValue = _verticalOffset, targetValue = offset) { value, _ ->
            _verticalOffset = value
        }
    }

    private fun calculateVerticalOffset(): Float = when {
        // If drag hasn't gone past the threshold, the position is the adjustedDistancePulled.
        adjustedDistancePulled <= positionalThreshold -> adjustedDistancePulled
        else -> {
            // How far beyond the threshold pull has gone, as a percentage of the threshold.
            val overshootPercent = abs(progress) - 1.0f
            // Limit the overshoot to 200%. Linear between 0 and 200.
            val linearTension = overshootPercent.coerceIn(0f, 2f)
            // Non-linear tension. Increases with linearTension, but at a decreasing rate.
            val tensionPercent = linearTension - linearTension.pow(2) / 4
            // The additional offset beyond the threshold.
            val extraOffset = positionalThreshold * tensionPercent
            positionalThreshold + extraOffset
        }
    }

    companion object {
        fun Saver(
            positionalThreshold: Float,
            enabled: () -> Boolean,
            coroutineScope: CoroutineScope
        ) = androidx.compose.runtime.saveable.Saver<PullToRefreshState, Boolean>(
            save = { it.isRefreshing },
            restore = { isRefreshing ->
                DownsidePullToRefreshState(
                    initialRefreshing = isRefreshing,
                    positionalThreshold = positionalThreshold,
                    enabled = enabled,
                    coroutineScope = coroutineScope
                )
            }
        )
    }

    private var distancePulled by mutableFloatStateOf(0f)
    private val adjustedDistancePulled: Float get() = distancePulled * DragMultiplier
    private var _verticalOffset by mutableFloatStateOf(0f)
    private var _refreshing by mutableStateOf(initialRefreshing)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberDownsidePullToRefreshState(
    positionalThreshold: Dp = PullToRefreshDefaults.PositionalThreshold,
    enabled: () -> Boolean = { true },
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): PullToRefreshState {
    val density = LocalDensity.current
    val positionalThresholdPx = with(density) { positionalThreshold.toPx() }
    return rememberSaveable(
        positionalThresholdPx, enabled,
        saver = DownsidePullToRefreshState.Saver(
            positionalThreshold = positionalThresholdPx,
            enabled = enabled,
            coroutineScope = coroutineScope
        )
    ) {
        DownsidePullToRefreshState(
            initialRefreshing = false,
            positionalThreshold = positionalThresholdPx,
            enabled = enabled,
            coroutineScope = coroutineScope
        )
    }
}

@Composable
@ExperimentalMaterial3Api
@Suppress("ComposableLambdaParameterPosition")
fun DownsidePullToRefreshContainer(
    state: PullToRefreshState,
    modifier: Modifier = Modifier,
    indicator: @Composable (PullToRefreshState) -> Unit = { pullRefreshState ->
        PullToRefreshDefaults.Indicator(state = pullRefreshState)
    },
    shape: Shape = PullToRefreshDefaults.shape,
    containerColor: Color = PullToRefreshDefaults.containerColor,
    contentColor: Color = PullToRefreshDefaults.contentColor,
) {
    // Surface is not used here, as we do not want its input-blocking behaviour, since the indicator
    // is typically displayed above other (possibly) interactive indicator.
    val showElevation = remember {
        derivedStateOf { state.verticalOffset > 1f || state.isRefreshing }
    }
    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(
            modifier = modifier
                .size(SpinnerContainerSize)
                .graphicsLayer {
                    translationY = state.verticalOffset + size.height
                }
                .shadow(
                    // Avoid shadow when indicator is hidden
                    elevation = if (showElevation.value) Elevation else 0.dp,
                    shape = shape,
                    clip = true
                )
                .background(color = containerColor, shape = shape)
        ) {
            indicator(state)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private class UpsidePullToRefreshState(
    initialRefreshing: Boolean,
    override val positionalThreshold: Float,
    enabled: () -> Boolean,
    private val coroutineScope: CoroutineScope
) : PullToRefreshState {

    override val progress get() = adjustedDistancePulled / positionalThreshold
    override val verticalOffset get() = _verticalOffset

    override val isRefreshing get() = _refreshing

    override fun startRefresh() {
        _refreshing = true
        _verticalOffset = positionalThreshold
    }

    override fun endRefresh() {
        coroutineScope.launch { animateTo(0f) }
        _refreshing = false
    }

    override var nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(
            available: Offset,
            source: NestedScrollSource,
        ): Offset = when {
            !enabled() -> Offset.Zero
            // Swiping up
            source == NestedScrollSource.UserInput && available.y < 0 -> {
                consumeAvailableOffset(available)
            }

            else -> Offset.Zero
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset = when {
            !enabled() -> Offset.Zero
            // Swiping down
            source == NestedScrollSource.UserInput && available.y > 0 -> {
                consumeAvailableOffset(available)
            }

            else -> Offset.Zero
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            return Velocity(0f, onRelease(available.y))
        }
    }

    /** Helper method for nested scroll connection */
    fun consumeAvailableOffset(available: Offset): Offset {
        val y = if (isRefreshing) 0f else {
            val newOffset = (distancePulled + available.y).coerceAtLeast(0f)
            val dragConsumed = newOffset - distancePulled
            distancePulled = newOffset
            _verticalOffset = calculateVerticalOffset()
            dragConsumed
        }
        return Offset(0f, y)
    }

    suspend fun onRelease(velocity: Float): Float {
        if (isRefreshing) return 0f // Already refreshing, do nothing
        // Trigger refresh
        if (adjustedDistancePulled > positionalThreshold) {
            startRefresh()
        } else {
            animateTo(0f)
        }

        val consumed = when {
            // We are flinging without having dragged the pull refresh (for example a fling inside
            // a list) - don't consume
            distancePulled == 0f -> 0f
            // If the velocity is negative, the fling is upwards, and we don't want to prevent the
            // the list from scrolling
            velocity < 0f -> 0f
            // We are showing the indicator, and the fling is downwards - consume everything
            else -> velocity
        }
        distancePulled = 0f
        return consumed
    }

    suspend fun animateTo(offset: Float) {
        animate(initialValue = _verticalOffset, targetValue = offset) { value, _ ->
            _verticalOffset = value
        }
    }

    fun calculateVerticalOffset(): Float = when {
        // If drag hasn't gone past the threshold, the position is the adjustedDistancePulled.
        adjustedDistancePulled <= positionalThreshold -> adjustedDistancePulled
        else -> {
            // How far beyond the threshold pull has gone, as a percentage of the threshold.
            val overshootPercent = abs(progress) - 1.0f
            // Limit the overshoot to 200%. Linear between 0 and 200.
            val linearTension = overshootPercent.coerceIn(0f, 2f)
            // Non-linear tension. Increases with linearTension, but at a decreasing rate.
            val tensionPercent = linearTension - linearTension.pow(2) / 4
            // The additional offset beyond the threshold.
            val extraOffset = positionalThreshold * tensionPercent
            positionalThreshold + extraOffset
        }
    }

    companion object {
        fun Saver(
            positionalThreshold: Float,
            enabled: () -> Boolean,
            coroutineScope: CoroutineScope
        ) = androidx.compose.runtime.saveable.Saver<PullToRefreshState, Boolean>(
            save = { it.isRefreshing },
            restore = { isRefreshing ->
                UpsidePullToRefreshState(
                    initialRefreshing = isRefreshing,
                    positionalThreshold = positionalThreshold,
                    enabled = enabled,
                    coroutineScope = coroutineScope
                )
            }
        )
    }

    private var distancePulled by mutableFloatStateOf(0f)
    private val adjustedDistancePulled: Float get() = distancePulled * DragMultiplier
    private var _verticalOffset by mutableFloatStateOf(0f)
    private var _refreshing by mutableStateOf(initialRefreshing)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberUpsidePullToRefreshState(
    positionalThreshold: Dp = PullToRefreshDefaults.PositionalThreshold,
    enabled: () -> Boolean = { true },
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): PullToRefreshState {
    val density = LocalDensity.current
    val positionalThresholdPx = with(density) { positionalThreshold.toPx() }
    return rememberSaveable(
        positionalThresholdPx, enabled,
        saver = UpsidePullToRefreshState.Saver(
            positionalThreshold = positionalThresholdPx,
            enabled = enabled,
            coroutineScope = coroutineScope
        )
    ) {
        UpsidePullToRefreshState(
            initialRefreshing = false,
            positionalThreshold = positionalThresholdPx,
            enabled = enabled,
            coroutineScope = coroutineScope
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpsidePullToRefreshContainer(
    onRefreshTrigger: CoroutineScope.() -> Job,
    modifier: Modifier = Modifier,
    shape: Shape = PullToRefreshDefaults.shape,
    containerColor: Color = MaterialTheme.colorScheme.onPrimary,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable BoxScope.(isRefreshing: Boolean) -> Unit
) {
    val pullToRefreshState = rememberUpsidePullToRefreshState()
    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(Unit) {
            onRefreshTrigger().join()
            pullToRefreshState.endRefresh()
        }
    }
    Box(modifier = modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
        content(pullToRefreshState.isRefreshing)
        PullToRefreshContainer(
            state = pullToRefreshState,
            shape = shape,
            contentColor = contentColor,
            containerColor = containerColor,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BothDirectedPullToRefreshContainer(
    onRefreshTrigger: CoroutineScope.() -> Job,
    modifier: Modifier = Modifier,
    shape: Shape = PullToRefreshDefaults.shape,
    containerColor: Color = MaterialTheme.colorScheme.onPrimary,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable BoxScope.(isRefreshing: Boolean) -> Unit
) {
    val upsidePullToRefreshState = rememberUpsidePullToRefreshState()
    val downsidePullToRefreshState = rememberDownsidePullToRefreshState()
    val isRefreshing by remember {
        derivedStateOf {
            upsidePullToRefreshState.isRefreshing || downsidePullToRefreshState.isRefreshing
        }
    }
    if (isRefreshing) {
        LaunchedEffect(Unit) {
            onRefreshTrigger().join()
            upsidePullToRefreshState.endRefresh()
            downsidePullToRefreshState.endRefresh()
        }
    }
    Box(
        modifier = modifier
            .nestedScroll(upsidePullToRefreshState.nestedScrollConnection)
            .nestedScroll(downsidePullToRefreshState.nestedScrollConnection)
    ) {
        content(isRefreshing)
        PullToRefreshContainer(
            state = upsidePullToRefreshState,
            shape = shape,
            contentColor = contentColor,
            containerColor = containerColor,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        DownsidePullToRefreshContainer(
            state = downsidePullToRefreshState,
            shape = shape,
            contentColor = contentColor,
            containerColor = containerColor,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}