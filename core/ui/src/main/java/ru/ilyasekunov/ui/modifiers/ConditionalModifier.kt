package ru.ilyasekunov.ui.modifiers

import androidx.compose.ui.Modifier

fun Modifier.conditional(
    condition: Boolean,
    block: Modifier.() -> Modifier
): Modifier = if (condition) {
    this.block()
} else this

fun Modifier.conditional(
    condition: Boolean,
    trueBlock: Modifier.() -> Modifier,
    falseBlock: Modifier.() -> Modifier
): Modifier = if (condition) {
    this.trueBlock()
} else this.falseBlock()