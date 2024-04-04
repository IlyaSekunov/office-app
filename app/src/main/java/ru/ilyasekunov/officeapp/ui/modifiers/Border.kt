package ru.ilyasekunov.officeapp.ui.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

enum class BorderSide {
    TOP, BOTTOM
}

fun Modifier.border(
    borderSide: BorderSide,
    strokeWidth: Dp,
    color: Color
): Modifier = when (borderSide) {
    BorderSide.TOP -> topBorder(strokeWidth, color)
    BorderSide.BOTTOM -> bottomBorder(strokeWidth, color)
}

private fun Modifier.topBorder(
    strokeWidth: Dp,
    color: Color
): Modifier = this.drawBehind {
    drawLine(
        color = color,
        start = Offset(x = 0F, y = 0F),
        end = Offset(x = size.width, y = 0F),
        strokeWidth = strokeWidth.toPx()
    )
}

private fun Modifier.bottomBorder(
    strokeWidth: Dp,
    color: Color
): Modifier = this.drawBehind {
    drawLine(
        color = color,
        start = Offset(x = 0F, y = size.height),
        end = Offset(x = size.width, y = size.height),
        strokeWidth = strokeWidth.toPx()
    )
}