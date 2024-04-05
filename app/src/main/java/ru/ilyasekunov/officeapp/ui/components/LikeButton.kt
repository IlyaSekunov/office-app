package ru.ilyasekunov.officeapp.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.ui.animations.likePressedAnimation
import ru.ilyasekunov.officeapp.ui.modifiers.conditional
import ru.ilyasekunov.officeapp.ui.theme.likePressedColor
import ru.ilyasekunov.officeapp.util.toThousandsString

private class LikeAnimationState(
    initialIconRotation: Float = 0f,
    initialIconScale: Float = 1f,
    val coroutineScope: CoroutineScope
) {
    val iconRotation = Animatable(initialIconRotation)
    val iconScale = Animatable(initialIconScale)

    fun animate() {
        coroutineScope.likePressedAnimation(
            animatableScale = iconScale,
            animatableRotation = iconRotation
        )
    }
}

@Composable
private fun rememberLikeAnimationState(
    initialIconRotation: Float = 0f,
    initialIconScale: Float = 1f,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): LikeAnimationState = remember(coroutineScope) {
    LikeAnimationState(
        initialIconRotation,
        initialIconScale,
        coroutineScope
    )
}

@Composable
fun LikeButton(
    onClick: () -> Unit,
    iconSize: Dp,
    textSize: TextUnit,
    isPressed: Boolean,
    count: Int,
    modifier: Modifier = Modifier,
    withBackground: Boolean = true,
    withRippleEffect: Boolean = true
) {
    val color = if (isPressed) likePressedColor else MaterialTheme.colorScheme.surfaceVariant
    val likeAnimationState = rememberLikeAnimationState()
    val onLikeClick = remember(onClick, likeAnimationState) {
        {
            onClick()
            if (!isPressed) {
                likeAnimationState.animate()
            }
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .conditional(
                condition = withRippleEffect,
                trueBlock = { clickable(onClick = onLikeClick) },
                falseBlock = {
                    clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = onLikeClick
                    )
                }
            )
            .conditional(withBackground) { background(color.copy(alpha = 0.2f)) }
    ) {
        Icon(
            painter = painterResource(R.drawable.outline_thumb_up_24),
            contentDescription = "like_icon",
            tint = color,
            modifier = Modifier
                .padding(start = 10.dp, top = 6.dp, end = 7.dp, bottom = 6.dp)
                .size(iconSize)
                .graphicsLayer {
                    scaleX = likeAnimationState.iconScale.value
                    scaleY = likeAnimationState.iconScale.value
                    transformOrigin = TransformOrigin(0f, 1f)
                    rotationZ = likeAnimationState.iconRotation.value
                }
        )
        Text(
            text = count.toThousandsString(),
            style = MaterialTheme.typography.labelMedium,
            fontSize = textSize,
            color = color,
            modifier = Modifier.padding(end = 10.dp)
        )
    }
}