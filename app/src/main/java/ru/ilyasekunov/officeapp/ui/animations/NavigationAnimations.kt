package ru.ilyasekunov.officeapp.ui.animations

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry

fun AnimatedContentTransitionScope<NavBackStackEntry>.exitSlideRight() =
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween()
    )

fun AnimatedContentTransitionScope<NavBackStackEntry>.enterSlideLeft() =
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween()
    )

fun AnimatedContentTransitionScope<NavBackStackEntry>.enterSlideUp() =
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Up,
        animationSpec = tween()
    )

fun AnimatedContentTransitionScope<NavBackStackEntry>.exitSlideDown() =
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Down,
        animationSpec = tween()
    )


