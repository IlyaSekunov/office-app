package ru.ilyasekunov.officeapp.ui.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun CoroutineScope.concurrentScaleRotationAnimation(
    animatableScale: Animatable<Float, AnimationVector1D>,
    animatableRotation: Animatable<Float, AnimationVector1D>,
    targetScale: Float,
    targetRotation: Float,
    durationMillis: Int
) = launch {
    launch {
        animatableScale.animateTo(
            targetValue = targetScale,
            animationSpec = tween(durationMillis)
        )
    }
    launch {
        animatableRotation.animateTo(
            targetValue = targetRotation,
            animationSpec = tween(durationMillis)
        )
    }
}

fun CoroutineScope.likePressedAnimation(
    animatableScale: Animatable<Float, AnimationVector1D>,
    animatableRotation: Animatable<Float, AnimationVector1D>
) = launch {
    concurrentScaleRotationAnimation(
        animatableScale,
        animatableRotation,
        targetScale = 1.2f,
        targetRotation = -30f,
        durationMillis = 500
    ).join()
    concurrentScaleRotationAnimation(
        animatableScale,
        animatableRotation,
        targetScale = 0.8f,
        targetRotation = 10f,
        durationMillis = 400
    ).join()
    concurrentScaleRotationAnimation(
        animatableScale,
        animatableRotation,
        targetScale = 1f,
        targetRotation = 0f,
        durationMillis = 400
    )
}

fun CoroutineScope.dislikePressedAnimation(
    animatableScale: Animatable<Float, AnimationVector1D>,
    animatableRotation: Animatable<Float, AnimationVector1D>
) = launch {
    concurrentScaleRotationAnimation(
        animatableScale,
        animatableRotation,
        targetScale = 0.8f,
        targetRotation = 30f,
        durationMillis = 400
    ).join()
    concurrentScaleRotationAnimation(
        animatableScale,
        animatableRotation,
        targetScale = 1.2f,
        targetRotation = -20f,
        durationMillis = 500
    ).join()
    concurrentScaleRotationAnimation(
        animatableScale,
        animatableRotation,
        targetScale = 1f,
        targetRotation = 0f,
        durationMillis = 400
    )
}
