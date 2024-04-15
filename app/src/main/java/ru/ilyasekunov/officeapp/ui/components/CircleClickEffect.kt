package ru.ilyasekunov.officeapp.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private class CircleClickEffectState(
    private val targetCircleRadiusPercent: Float = 0.8f,
    private val initialCircleRadiusPercent: Float = targetCircleRadiusPercent + 0.3f,
    private val initialAlpha: Float = 0f,
    private val targetAlpha: Float = 1f,
    private val coroutineScope: CoroutineScope,
    private val interactionSource: MutableInteractionSource,
    val animationSpec: AnimationSpec<Float> = tween(100)
) {
    val alpha = Animatable(initialAlpha)
    val circleRadiusPercent = Animatable(initialCircleRadiusPercent)

    init {
        coroutineScope.launch {
            interactionSource.interactions.collect {
                when (it) {
                    is PressInteraction.Press -> appearingAnimate()
                    is PressInteraction.Cancel -> disappearingAnimate()
                    is PressInteraction.Release -> disappearingAnimate()
                }
            }
        }
    }

    private fun appearingAnimate() {
        coroutineScope.launch {
            alpha.animateTo(targetAlpha, animationSpec)
        }
        coroutineScope.launch {
            circleRadiusPercent.animateTo(targetCircleRadiusPercent, animationSpec)
        }
    }

    private fun disappearingAnimate() {
        coroutineScope.launch {
            alpha.animateTo(initialAlpha, animationSpec)
        }
        coroutineScope.launch {
            circleRadiusPercent.animateTo(initialCircleRadiusPercent, animationSpec)
        }
    }
}

@Composable
private fun rememberCircleClickEffectState(
    targetCircleRadiusPercent: Float = 0.8f,
    initialCircleRadiusPercent: Float = targetCircleRadiusPercent + 0.3f,
    initialAlpha: Float = 0f,
    targetAlpha: Float = 1f,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    animationSpec: AnimationSpec<Float> = tween(100)
) = remember(
    targetAlpha,
    targetCircleRadiusPercent,
    initialAlpha,
    initialCircleRadiusPercent,
    coroutineScope,
    interactionSource,
    animationSpec
) {
    CircleClickEffectState(
        targetCircleRadiusPercent = targetCircleRadiusPercent,
        initialCircleRadiusPercent = initialCircleRadiusPercent,
        initialAlpha = initialAlpha,
        targetAlpha = targetAlpha,
        coroutineScope = coroutineScope,
        interactionSource = interactionSource,
        animationSpec = animationSpec
    )
}

@Composable
fun CircleClickEffect(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    circleColor: Color = Color.Black.copy(alpha = 0.15f),
    circleSizePercent: Float = 0.8f,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val circleAnimationState = rememberCircleClickEffectState(
        targetCircleRadiusPercent = circleSizePercent,
        interactionSource = interactionSource
    )
    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .drawBehind {
                drawCircle(
                    color = circleColor,
                    radius = size.minDimension * circleAnimationState.circleRadiusPercent.value,
                    alpha = circleAnimationState.alpha.value
                )
            }
    ) {
        content()
    }
}