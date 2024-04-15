package ru.ilyasekunov.officeapp.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CircleClickEffectIndication(
    val circleColor: Color = Color.Black.copy(alpha = 0.15f),
    val radiusPercent: Float = 0.8f,
    val coroutineScope: CoroutineScope
) : Indication {
    private inner class CircleClickEffectIndicationInstance(
        private val isPressed: State<Boolean>,
        private val isHovered: State<Boolean>,
        private val isFocused: State<Boolean>,
    ) : IndicationInstance {
        private val initialAlpha = 0f
        private val targetAlpha = 1f
        private val alphaAnimationSpec = tween<Float>(50)
        private val targetCircleRadiusPercent = radiusPercent
        private val initialCircleRadiusPercent = targetCircleRadiusPercent + 0.4f
        private val radiusAnimationSpec = tween<Float>(70)

        private val alpha = Animatable(initialAlpha)
        private val circleRadiusPercent = Animatable(initialCircleRadiusPercent)

        override fun ContentDrawScope.drawIndication() {
            drawContent()
            if (isPressed.value || isHovered.value || isFocused.value) {
                animateAppearing()
            } else {
                animateDisappearing()
            }
            drawCircle(
                color = circleColor,
                alpha = alpha.value,
                radius = size.minDimension * circleRadiusPercent.value
            )
        }

        private fun animateAppearing() {
            coroutineScope.launch {
                launch {
                    alpha.animateTo(targetAlpha, alphaAnimationSpec)
                }
                launch {
                    circleRadiusPercent.animateTo(targetCircleRadiusPercent, radiusAnimationSpec)
                }
            }
        }

        private fun animateDisappearing() {
            coroutineScope.launch {
                launch {
                    alpha.animateTo(initialAlpha, alphaAnimationSpec)
                }
                launch {
                    circleRadiusPercent.animateTo(initialCircleRadiusPercent, radiusAnimationSpec)
                }
            }
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val isPressed = interactionSource.collectIsPressedAsState()
        val isHovered = interactionSource.collectIsHoveredAsState()
        val isFocused = interactionSource.collectIsFocusedAsState()
        return remember(interactionSource) {
            CircleClickEffectIndicationInstance(isPressed, isHovered, isFocused)
        }
    }
}

@Composable
fun rememberCircleClickEffectIndication(
    circleColor: Color = Color.Black.copy(alpha = 0.15f),
    radiusPercent: Float = 0.8f,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(circleColor, radiusPercent, coroutineScope) {
    CircleClickEffectIndication(circleColor, radiusPercent, coroutineScope)
}