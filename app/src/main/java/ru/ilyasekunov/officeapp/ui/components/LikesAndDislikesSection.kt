package ru.ilyasekunov.officeapp.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
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
        Spacer(modifier = Modifier.width(spaceBetweenCategories))
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