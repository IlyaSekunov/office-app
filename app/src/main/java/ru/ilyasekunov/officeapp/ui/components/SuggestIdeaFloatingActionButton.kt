package ru.ilyasekunov.officeapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.ilyasekunov.officeapp.R

class SuggestIdeaFABState(isVisible: Boolean) {
    var isVisible by mutableStateOf(isVisible)
}

abstract class SuggestIdeaFABScrollBehaviour(
    val state: SuggestIdeaFABState
) {
    abstract val nestedScrollConnection: NestedScrollConnection
}

class DefaultSuggestIdeaFABScrollBehaviour(
    state: SuggestIdeaFABState
) : SuggestIdeaFABScrollBehaviour(state) {
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
fun rememberSuggestIdeaFABState(
    isVisible: Boolean = true
): SuggestIdeaFABState = remember { SuggestIdeaFABState(isVisible) }

@Composable
fun defaultSuggestIdeaFABScrollBehaviour(
    state: SuggestIdeaFABState = rememberSuggestIdeaFABState()
): SuggestIdeaFABScrollBehaviour = remember(state) {
    DefaultSuggestIdeaFABScrollBehaviour(state)
}

@Composable
fun SuggestIdeaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehaviour: SuggestIdeaFABScrollBehaviour? = null
) {
    val iconButton: @Composable () -> Unit = remember(onClick, modifier) {
        {
            FloatingActionButton(
                onClick = onClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.Black,
                shape = MaterialTheme.shapes.medium,
                modifier = modifier
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_create_24),
                    contentDescription = "create_button",
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
    if (scrollBehaviour != null) {
        AnimatedVisibility(
            visible = scrollBehaviour.state.isVisible,
            enter = slideInVertically(
                animationSpec = tween(),
                initialOffsetY = { it / 2 }
            ) + fadeIn(tween(150)),
            exit = slideOutVertically(
                animationSpec = tween(),
                targetOffsetY = { it },
            ) + fadeOut(tween(150))
        ) {
            iconButton()
        }
    } else {
        iconButton()
    }
}