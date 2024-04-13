package ru.ilyasekunov.officeapp.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.ui.suggestidea.CloseIconButton

data class AttachedImage(
    val id: Int,
    val image: Any
)

private class AttachedImageUiState(
    initialRotation: Float = 0f,
    initialScale: Float = 1f,
    initialAlpha: Float = 1f,
    private val coroutineScope: CoroutineScope
) {
    val rotation = Animatable(initialRotation)
    val scale = Animatable(initialScale)
    val alpha = Animatable(initialAlpha)

    suspend fun animateRemoving() {
        coroutineScope.launch {
            launch {
                rotation.animateTo(
                    45f,
                    animationSpec = tween()
                )
            }
            launch {
                scale.animateTo(
                    0.2f,
                    animationSpec = tween()
                )
            }
            launch {
                alpha.animateTo(
                    0f,
                    animationSpec = tween()
                )
            }
        }.join()
    }
}

@Composable
private fun rememberAttachedImageUiState(
    initialRotation: Float = 0f,
    initialScale: Float = 1f,
    initialAlpha: Float = 1f,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): AttachedImageUiState = remember(coroutineScope) {
    AttachedImageUiState(
        initialRotation,
        initialScale,
        initialAlpha,
        coroutineScope
    )
}

@Composable
fun AttachedImage(
    image: AttachedImage,
    attachedImageSize: DpSize,
    onRemoveClick: () -> Unit,
    closeIconSize: Dp,
    modifier: Modifier = Modifier,
    closeIconRightCornerOffsetPercent: Float = 0.05f
) {
    val attachedImageUiState = rememberAttachedImageUiState()
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = attachedImageUiState.scale.value
                scaleY = attachedImageUiState.scale.value
                rotationZ = attachedImageUiState.rotation.value
                alpha = attachedImageUiState.alpha.value
            }
    ) {
        AsyncImageWithLoading(
            model = image.image,
            modifier = Modifier.size(attachedImageSize)
        )

        val closeIconButtonRightCornerTopPadding =
            (attachedImageSize.height.value * closeIconRightCornerOffsetPercent).dp
        val closeIconButtonRightCornerRightPadding =
            (attachedImageSize.width.value * closeIconRightCornerOffsetPercent).dp
        CloseIconButton(
            onClick = {
                coroutineScope.launch {
                    attachedImageUiState.animateRemoving()
                    onRemoveClick()
                }
            },
            modifier = Modifier
                .padding(
                    top = closeIconButtonRightCornerTopPadding,
                    end = closeIconButtonRightCornerRightPadding
                )
                .size(closeIconSize)
                .clip(CircleShape)
                .background(Color.White)
                .align(Alignment.TopEnd)
        )
    }
}

@Composable
fun AttachedImages(
    images: List<AttachedImage>,
    imageSize: DpSize,
    onRemoveClick: (AttachedImage) -> Unit,
    contentPadding: PaddingValues,
    closeIconSize: Dp,
    modifier: Modifier = Modifier
) {
    LazyRow(
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
    ) {
        items(
            count = images.size,
            key = { images[it].id }
        ) {
            AttachedImage(
                image = images[it],
                attachedImageSize = imageSize,
                onRemoveClick = { onRemoveClick(images[it]) },
                closeIconSize = closeIconSize,
                modifier = Modifier.clip(MaterialTheme.shapes.small)
            )
        }
    }
}