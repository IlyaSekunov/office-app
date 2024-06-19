package ru.ilyasekunov.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

@Composable
fun LikesAndDislikesSection(
    isLikePressed: Boolean,
    likesCount: Int,
    onLikeClick: () -> Unit,
    isDislikePressed: Boolean,
    dislikesCount: Int,
    onDislikeClick: () -> Unit,
    spaceBetweenCategories: Dp,
    likesIconSize: Dp,
    dislikesIconSize: Dp,
    textSize: TextUnit,
    buttonsWithBackground: Boolean,
    buttonsWithRippleEffect: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spaceBetweenCategories),
        modifier = modifier
    ) {
        LikeButton(
            onClick = onLikeClick,
            iconSize = likesIconSize,
            textSize = textSize,
            isPressed = isLikePressed,
            count = likesCount,
            withBackground = buttonsWithBackground,
            withRippleEffect = buttonsWithRippleEffect
        )
        DislikeButton(
            onClick = onDislikeClick,
            iconSize = dislikesIconSize,
            textSize = textSize,
            isPressed = isDislikePressed,
            count = dislikesCount,
            withBackground = buttonsWithBackground,
            withRippleEffect = buttonsWithRippleEffect
        )
    }
}