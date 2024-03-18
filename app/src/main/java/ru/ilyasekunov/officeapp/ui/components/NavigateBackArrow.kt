package ru.ilyasekunov.officeapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class NavigateBackArrowState(isVisible: Boolean) {
    var isVisible by mutableStateOf(isVisible)
}

abstract class NavigateBackArrowScrollBehaviour(val state: NavigateBackArrowState) {
    abstract val nestedScrollConnection: NestedScrollConnection
}

class DefaultBackArrowScrollBehaviour(
    state: NavigateBackArrowState
) : NavigateBackArrowScrollBehaviour(state) {
    override val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (available.y > 0) {
                state.isVisible = true
            } else if (available.y < 0) {
                state.isVisible = false
            }
            return super.onPreScroll(available, source)
        }
    }
}

@Composable
fun rememberNavigateBackArrowState(
    isVisible: Boolean = true
): NavigateBackArrowState = remember { NavigateBackArrowState(isVisible) }

@Composable
fun defaultNavigateBackArrowScrollBehaviour(
    state: NavigateBackArrowState = rememberNavigateBackArrowState()
): NavigateBackArrowScrollBehaviour = DefaultBackArrowScrollBehaviour(state)

@Composable
fun NavigateBackArrow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 30.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    scrollBehaviour: NavigateBackArrowScrollBehaviour? = null
) {
    val iconButton: @Composable () -> Unit = {
        IconButton(
            onClick = onClick,
            colors = IconButtonDefaults.outlinedIconButtonColors(
                contentColor = color
            ),
            modifier = modifier
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "arrow_back",
                modifier = Modifier.size(iconSize)
            )
        }
    }
    if (scrollBehaviour != null) {
        AnimatedVisibility(
            visible = scrollBehaviour.state.isVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            iconButton()
        }
    } else {
        iconButton()
    }
}