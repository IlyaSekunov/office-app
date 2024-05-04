package ru.ilyasekunov.officeapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun Scrim(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.Black.copy(alpha = 0.6f)
) {
    Box(
        modifier = modifier
            .pointerInput(onClick) { detectTapGestures { onClick() } }
            .onKeyEvent {
                if (it.key == Key.Escape) {
                    onClick()
                    true
                } else {
                    false
                }
            }
            .background(color)
    )
}